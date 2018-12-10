package cn.lw.tv.douban.fm;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Controller
public class FMController {

    @Autowired
    private FMService fmService;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request, @CookieValue(name="un",defaultValue="") String un, HttpServletResponse response) {
        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");

        // 刷新缓存
        if(StringUtils.hasText(username) && StringUtils.hasText(pwd)) {
            List<Song> refresh = fmService.refresh(username, pwd);
            if(refresh.size() > 0) {
                response.addCookie(new Cookie("un", username));
            } else {
                Cookie ck = new Cookie("un", "");
                ck.setMaxAge(-1);
                response.addCookie(ck);
            }
        } else if(StringUtils.hasText(un)) {
        } else {
            return "index";
        }
        return "redirect:/fm/main";
    }

    @RequestMapping("/clear")
    public String clear(HttpServletResponse response) {
        Cookie ck = new Cookie("un", "");
        ck.setMaxAge(-1);
        response.addCookie(ck);
        return  "index";
    }

    @RequestMapping("/fm/main")
    public String main(Model model, @CookieValue(name="un",defaultValue="") String un, HttpServletResponse response) {
        List<Song> songs = fmService.getCache(un);
        List<Song> sub = songs;
        model.addAttribute("songs", sub);
        return "main";
    }

    @RequestMapping("/fm/refresh")
    public String refresh() {
        return "index";
    }

    @RequestMapping("/fm/repeat/{sid}")
    @ResponseBody
    public String repeat(HttpServletRequest request, @PathVariable("sid") String sid, @CookieValue(name="un") String un) {
        String repeat = request.getParameter("repeat");
        fmService.changeRepeat(un, sid, "1".equals(repeat) ? true : false);
        return "{\"success\":\"ok\"}";
    }

    @RequestMapping("/fm/img")
    public void img(@RequestParam("p") String url, HttpServletResponse response) throws IOException {
        HttpGet get = new HttpGet(url);
        get.addHeader("referer", "https://douban.fm/");
        CloseableHttpResponse execute = HttpClients.createDefault().execute(get);
        InputStream inputStream = execute.getEntity().getContent();
        response.setContentType("image/jpeg");
        ServletOutputStream os = response.getOutputStream();
        IOUtils.copy(inputStream, os);
        os.flush();
        os.close();
    }

    @RequestMapping("/fm/dowload")
    public void download(@CookieValue(name="un") String un, HttpServletResponse response) throws IOException {
        List<Song> songs = fmService.getCache(un);
        StringBuilder builder = new StringBuilder();
        for(Song song : songs) {
            builder.append(song.getTitle()).append("\n").append(song.getUrl()).append("\n");
            for(Singer singer : song.getSingers()) {
                builder.append(singer.getName()).append("|");
            }
            builder.append("\n\n");
        }
        response.setHeader("Content-Disposition", "attachment;filename=Songs.txt");
//        response.setHeader("Content-Type", "text/plain");
        ServletOutputStream os = response.getOutputStream();
        IOUtils.write(builder.toString().getBytes(), os);
        os.flush();
        os.close();
    }
}
