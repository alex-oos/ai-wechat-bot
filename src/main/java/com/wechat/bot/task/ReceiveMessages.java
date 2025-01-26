package com.wechat.bot.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Alex
 * @since 2025/1/26 14:12
 * <p></p>
 */
@Component
public class ReceiveMessages  implements CommandLineRunner {

    @Resource()
    private MessageQueue taskQueue;


    @Override
    public void run(String... args) throws Exception {


    }

}
