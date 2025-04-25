package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.dto.FriendDTO;
import com.wechat.bot.mapper.FriendMapper;
import com.wechat.bot.service.ChatRoomService;
import com.wechat.bot.service.FriendService;
import com.wechat.gewechat.service.ContactApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Alex
 * @since 2025/1/27 22:25
 * <p></p>
 */
@Slf4j
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, FriendDTO> implements FriendService {

    @Resource
    private BotConfig botConfig;

    @Resource
    private ChatRoomService chatRoomService;

    @Override
    public void syncContacts() {

        JSONObject response = ContactApi.fetchContactsList(botConfig.getAppId());
        if (response.getInteger("ret") != 200) {
            log.error("获取好友列表失败");
            response = ContactApi.fetchContactsListCache(botConfig.getAppId());
            if (response.getInteger("ret") != 200) {
                log.error("获取好友列表失败");
                return;
            }
            return;
        }

        JSONObject data = response.getJSONObject("data");
        JSONArray friends = data.getJSONArray("friends");
        JSONArray chatrooms = data.getJSONArray("chatrooms");
        List<String> friendIds = friends.toJavaList(String.class);
        friendIds.removeIf(friendId -> !friendId.startsWith("wxid_"));
        List<String> chatroomIds = chatrooms.toJavaList(String.class);
        chatroomIds.removeIf(chatroomId -> !chatroomId.endsWith("@chatroom"));

        if (!friendIds.isEmpty()) {
            syncFriends(friendIds);

        }
        if (!chatroomIds.isEmpty()) {
            chatRoomService.syncChatRooms(chatroomIds);
        }


    }

    @Override
    public void syncFriends(List<String> friendIds) {


        this.remove(null);

        List<List<String>> lists = splitIntoGroups(friendIds, 20);
        for (List<String> list : lists) {
            getFriendInfo(list);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public String getFriendName(String userId) {
        /**
         * 先去查询看看是否存在，如果不存在，再调用接口获取，然后保存到数据库中，再返回
         *
         */
        LambdaQueryWrapper<FriendDTO> queryWrapper = new QueryWrapper<FriendDTO>().lambda();
        queryWrapper.eq(FriendDTO::getUserName, userId);
        FriendDTO friendDTO = this.getOne(queryWrapper);
        if (friendDTO != null) {
            return friendDTO.getNickName();
        }
        FriendDTO friendDTO1 = this.getFriendInfo(Collections.singletonList(userId)).stream().findFirst().orElseThrow(() -> new RuntimeException("获取好友信息失败"));
        return friendDTO1.getNickName();
    }

    @Transactional
    public List<FriendDTO> getFriendInfo(List<String> list) {

        JSONObject response = ContactApi.getDetailInfo(botConfig.getAppId(), list);
        if (response.getInteger("ret") != 200) {
            log.error("获取好友列表失败");
        }
        JSONArray data = response.getJSONArray("data");

        List<FriendDTO> friendDTOList = null;

        friendDTOList = data.toList(FriendDTO.class);

        boolean saveOrUpdateBatch = this.saveBatch(friendDTOList);
        if (!saveOrUpdateBatch) {
            log.error("保存好友列表失败");
            throw new RuntimeException("保存好友列表失败");
        }
        return friendDTOList;
    }

    public <T> List<List<T>> splitIntoGroups(List<T> originalList, int groupSize) {

        if (groupSize <= 0) throw new IllegalArgumentException("分组大小必须为正数");
        List<List<T>> result = new ArrayList<>();
        int totalSize = originalList.size();

        for (int i = 0; i < totalSize; i += groupSize) {
            int end = Math.min(i + groupSize, totalSize);
            List<T> group = new ArrayList<>(originalList.subList(i, end));
            result.add(group);
        }
        return result;
    }


}
