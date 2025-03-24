package com.wechat.schedule;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PersonSchedule extends AbstractSchedule {

    @Override
    protected List<String> getContactList() {
        // 确保这些名称与 userInfoService.getUserInfo() 返回的名称一致
        return Arrays.asList("爸爸", "妈妈", "爷爷", "奶奶");
    }
}
