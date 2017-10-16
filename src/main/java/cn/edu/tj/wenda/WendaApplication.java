package cn.edu.tj.wenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
/**
 * Created by kang on 2017/10/16.
 */
@SpringBootApplication
public class WendaApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(WendaApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(WendaApplication.class);
	}
}
