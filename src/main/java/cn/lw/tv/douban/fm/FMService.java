package cn.lw.tv.douban.fm;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FMService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public FMService() {
    }

    public List<Song> getCache(String userName) {
        String s = stringRedisTemplate.opsForValue().get("douban:red_songs:" + userName);
        List<Song> songs = JSON.parseArray(s, Song.class);
        return songs;
    }

    public List<Song> refresh(String userName, String pwd) {
        try {
            List<Song> songs = new HttpDemo().run(userName, pwd);
            System.out.println(songs.get(0));
            stringRedisTemplate.opsForValue().set("douban:red_songs:" + userName, JSON.toJSONString(songs));
            return songs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Song>();
    }

}
