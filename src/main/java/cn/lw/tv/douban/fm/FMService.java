package cn.lw.tv.douban.fm;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
public class FMService {

    @Autowired
    private StringRedisTemplate rt;

    public FMService() {
    }

    /**
     * 保存歌曲的喜好程度
     * @param sid 
     * @param isRepeat true 更喜欢 false 普通
     * @return 
     * @author VictorLiu 
     * @date 2018/12/7 14:58 
     */
    public void changeRepeat(String userName, String sid, boolean isRepeat) {
        // 获取高频歌曲sid
        String baseKey = "douban:repeat:"+userName;
        rt.opsForList().remove(baseKey, 0, sid);
        if(isRepeat) {
            rt.opsForList().leftPush(baseKey, sid);
        }
    }

    /*
    * 合并更喜欢的列表、普通列表，更喜欢的在前边
    **/
    public List<Song> getCache(String userName) {
        List<Song>[] list = getCacheBoth(userName);
        List<Song> songs = list[0];
        List<Song> songs_hot = list[1];
        songs_hot.addAll(songs);
        return songs_hot;
    }

    /*
    * 根据喜好程度，分成两个列表
    * 更喜欢的歌排在前边
    **/
    private List[] getCacheBoth(String userName) {
        List<Song> songs = new ArrayList<>();
        List<Song> songs_hot = new ArrayList<>();

        String baseKey = "douban:red_songs:"+userName;
        Set<String> keys = rt.keys(baseKey+":*");

        // 获取高频歌曲sid
        List<String> repeat = rt.opsForList().range("douban:repeat:"+userName, 0, 1000);
        String join = String.join(",", repeat)+",";

        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String val = rt.opsForValue().get(iterator.next());
            Song song = JSON.parseObject(val, Song.class);
            if(repeat.size() > 0 && join.contains(song.getSid()+",")) {
                song.setRepeat("1");
                songs_hot.add(song);
            } else {
                song.setRepeat("0");
                songs.add(song);
            }
        }
        return new List[]{songs, songs_hot};
    }

    /**
     * 获取红心列表并储存
     */
    public List<Song> refresh(String userName, String pwd) {
        try {
            List<Song> songs = new HttpDemo().run(userName, pwd);
            ValueOperations<String, String> ops = rt.opsForValue();
            for(Song song : songs) {
                ops.set("douban:red_songs:" + userName + ":" + song.getSid(), JSON.toJSONString(song));
            }
            return songs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Song>();
    }

}
