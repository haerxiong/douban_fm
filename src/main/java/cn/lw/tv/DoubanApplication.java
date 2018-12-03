package cn.lw.tv;

import cn.lw.tv.douban.fm.FMService;
import cn.lw.tv.douban.fm.Song;
import com.alibaba.fastjson.JSON;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class DoubanApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoubanApplication.class, args);
	}
}
