package cn.edu.tj.wenda;

import cn.edu.tj.wenda.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by mao on 2017/5/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class LikeServiceTest {
    @Autowired
    LikeService likeService;
    @Before
    public void serUp(){
        //初始化数据
        System.out.println("setUp");
    }
    @After
    public void tearDown(){
        //清理工作
        System.out.println("tearDown");
    }

    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }
    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }

    @Test
    public void testLike(){
        System.out.println("testLike");
        likeService.like(12,1,1);
        Assert.assertEquals(1,likeService.getLikeStatus(12,1,1));

        likeService.dislike(12,1,1);
        Assert.assertEquals(-1,likeService.getLikeStatus(12,1,1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException(){
        System.out.println("testException");
        throw new IllegalArgumentException("exception");
    }

}
