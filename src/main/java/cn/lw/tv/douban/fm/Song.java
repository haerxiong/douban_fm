package cn.lw.tv.douban.fm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by VictorLiu on 2018/11/30.
 */
public class Song {

    private String sid;
    private String title;
    private String picture;
    private String url;
    private List<Singer> singers;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Singer> getSingers() {
        return singers;
    }

    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    @Override
    public String toString() {
        return "Song{" +
                "sid='" + sid + '\'' +
                ", title='" + title + '\'' +
                ", picture='" + picture + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
