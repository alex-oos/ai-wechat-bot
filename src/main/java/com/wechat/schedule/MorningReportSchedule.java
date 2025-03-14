package com.wechat.schedule;

import com.wechat.ai.session.Session;
import com.wechat.bot.entity.ChatMessage;
import com.wechat.bot.enums.MsgTypeEnum;
import com.wechat.bot.service.MessageService;
import com.wechat.bot.service.ReplyMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex
 * @since 2025/3/14 15:01
 * <p>
 * 早安寄语定时任务
 * </p>
 */
@Slf4j
@Component
public class MorningReportSchedule {

    @Resource
    private MessageService messageService;

    @Resource
    private ReplyMsgService replyMsgService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void morningReport() {

        List<String> contactList = new ArrayList<>();
        // 早安寄语的制定人
        Collections.addAll(contactList, "爸爸", "妈妈", "爷爷", "奶奶");
        Map<String, String> contactMap = messageService.getContactMap();
        Set<String> cantactSet = contactMap.entrySet()
                .stream()
                .filter(entry -> contactList.contains(entry.getValue()))
                .map(e -> e.getKey() + "-" + e.getValue())
                .collect(Collectors.toSet());
        if (cantactSet.isEmpty()) {
            return;
        }
        cantactSet.forEach(contact -> {
            String[] split = contact.split("-");
            String content = "这是我的" + split[1] + "生成以" + split[1] + "为关键词的早安寄语";
            ChatMessage chatMessage = ChatMessage.builder()
                    .fromUserId(split[0])
                    .toUserId(null)
                    .ctype(MsgTypeEnum.TEXT)
                    //.content("这是我的" + split[1] + "生成以" + split[1] + "为关键词的早安寄语")
                    .appId(null)
                    .build();
            Session session = new Session(contact, null);
            session.addQuery(content);
            replyMsgService.replyTextMsg(chatMessage, session);
        });
        log.info("早安寄语发送成功！");

    }

}
