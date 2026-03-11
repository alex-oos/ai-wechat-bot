package com.wechat.schedule;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GroupSchedule extends AbstractSchedule {

    @Override
    protected List<String> getContactList() {
        // 确保这些名称与 userInfoService.getUserInfo() 返回的名称一致
        return Arrays.asList("相亲相爱一家人", "程序员大佬群");
    }
}
