package com.example.rearend;

import com.example.rearend.entity.Writing;
import com.example.rearend.service.IWritingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest(classes = BlogServerApplication.class)
@RunWith(SpringRunner.class)
public class BlogApplicationTest {

    @Autowired
    private IWritingService writingService;


    @Test
    public void addWritingTest() {
        for (int i = 0; i < 10; i++) {
            Writing writing = new Writing();
            writing.setContent("# 关于我做过的两个单体项目一些心得\n" +
                    "\n" +
                    "## 第一个单体项目存在的问题\n" +
                    "\n" +
                    "- 我之前也做过一个单体的项目，但是那个项目的前端页面根本没有实现自适应化，那时候也不知道怎么搞，再加上代码很乱，改起来的也有些难度，就不想去改了，毕竟大家都知道，一坨代码能跑起来，最好就不要去动它了，不然就有可能跑不起来了，当然，这是开玩笑的，我的全部代码都有用 git 统一控制的，可以回退版本。\n" +
                    "- 还有就是项目的可用性，那个项目我原本的想法是模仿出一个学习通之类的，当然，只是初级版，不是说完成实现，我是想着实现一部分，最后呢，实现是实现了一些功能，但是感觉实用性不高，没有什么价值。\n" +
                    "- 但是也对我有一些特殊价值，算是给我练手吧，所以在一个学长的提议下我当时是拒绝做个人博客的，然后后面又仔细想了想，做个个人博客也挺不错的，可以记录我自己的一些心得感想，分享一些技术\n" +
                    "\n" +
                    "## 第二个单体项目的优缺点\n" +
                    "\n" +
                    "- 优点就是刚刚说过的实现自适应化，无论是电脑或者手机访问的页面都还看得下去，还有就是我原本第一个项目的后端是使用了 mybatis，得手写很多 sql 语句，这样不仅浪费时间，还使得代码容易出错，比较手写 sql 只要写错一个字母都会使整个程序无法运行，而我这个项目使用了 mybaits-plus，简化了代码，且错误率大幅下降，用代码替代了 sql 语句，例如下面的代码\n" +
                    "\n" +
                    "```\n" +
                    "UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();\n" +
                    "updateWrapper.lambda()\n" +
                    "             .eq(User::getId, userDTO.getId())\n" +
                    "             .set(User::getImage, userDTO.getImage())\n" +
                    "```\n" +
                    "- 缺点也有，毕竟一个项目不可能做到完美，我个人觉得页面还是不太美观，我在做这个自己的个人博客之前，也有过去看别人是怎么做的，别人的动态页面效果做的很美观，我的页面动态有，但是只能说是一般般。\n");
            writing.setTitle("111");
            writing.setCreateTime(new Date());
            writing.setUpdateTime(new Date());
            writing.setLabelName("[vue, javaweb, springboot, 心得]");
            writingService.save(writing);
        }
        System.out.println("插入完成");
    }



}
