package cn.lw.tv.douban.fm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

public class HttpDemo {

    public List<Song> run(String name, String pwd) throws Exception {
        String cookie = login(name, pwd);
        String reds = getReds(cookie);
        List<Song> songs = getSongs(cookie, reds);
        return songs;
    }

    public String login(String name, String pwd) throws Exception {
        name = URLEncoder.encode(name, "utf-8");
        pwd = URLEncoder.encode(pwd, "utf-8");
        HttpPost post = new HttpPost("https://accounts.douban.com/j/popup/login/basic");
        String str = "source=fm&referer=https%3A%2F%2Fdouban.fm%2F&ck=&name="+name+"&password="+pwd+"&captcha_solution=&captcha_id=";
        HttpEntity entity = new StringEntity(str, ContentType.APPLICATION_FORM_URLENCODED);
        post.setEntity(entity);

        // 请求
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 处理返回
        CloseableHttpResponse httpResponse = httpClient.execute(post);
        Header[] headers = httpResponse.getHeaders("Set-Cookie");
        for(Header h : headers) {
            if(h.getValue().contains("dbcl2")) {
                return h.getValue();
            }
        }
        return "";
    }

    public String getReds(String cookie) throws Exception {
        HttpPost post = new HttpPost("https://douban.fm/j/v2/redheart/basic");
        post.addHeader("Cookie", cookie);
        System.out.println("-------------------");
        System.out.println(cookie);

        // 请求
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 处理返回
        CloseableHttpResponse httpResponse = httpClient.execute(post);

        ByteOutputStream os = new ByteOutputStream();
        httpResponse.getEntity().writeTo(os);
        String s = os.toString();
        System.out.println(s);
        JSONObject rs = JSON.parseObject(s);
        os.close();
        JSONArray ja = (JSONArray) rs.get("songs");
        Iterator<Object> iterator = ja.iterator();
        StringBuilder sids = new StringBuilder();
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            sids.append("|").append(next.get("sid").toString());
        }
        return sids.toString().substring(1);
    }

    public List<Song> getSongs(String cookie, String sids) throws Exception {
        HttpPost post = new HttpPost("https://douban.fm/j/v2/redheart/songs");
        sids = URLEncoder.encode(sids, "utf-8");
        String str = "sids="+sids+"&kbps=192&ck=uf3K";
        HttpEntity entity = new StringEntity(str, ContentType.APPLICATION_FORM_URLENCODED);
        post.setEntity(entity);
        post.addHeader("Cookie", cookie);

        // 请求
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 处理返回
        CloseableHttpResponse httpResponse = httpClient.execute(post);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        System.out.println(statusCode);

        ByteOutputStream os = new ByteOutputStream();
        httpResponse.getEntity().writeTo(os);
        String s = os.toString();
        os.close();

        List<Song> songs = JSON.parseArray(s, Song.class);
        return songs;
    }
}
