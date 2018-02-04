package cn.edu.tj.wenda;

import cn.edu.tj.wenda.dao.QuestionDao;
import cn.edu.tj.wenda.dao.UserDao;
import cn.edu.tj.wenda.model.Question;
import cn.edu.tj.wenda.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {

	@Autowired
	UserDao userDao;

	@Autowired
	QuestionDao questionDao;

	@Test
	public void initDatabase(){
		Random random = new Random();

		for (int i = 0; i < 11; i++){
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
			user.setName(String.format("USER%d",i));
			user.setPassword("");
			user.setSalt("");
			userDao.addUser(user);

			user.setPassword("hhhh");
			userDao.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*i);
			question.setCreatedDate(date);
			question.setUserId(i);
			question.setTitle(String.format("TITLE{%d}",i));
			question.setContent(String.format("xixixixi Content %d",i));

			questionDao.addQuestion(question);
		}
//		System.out.println(questionDao.selectLatestQuestions(0,0,10));
	}

}
