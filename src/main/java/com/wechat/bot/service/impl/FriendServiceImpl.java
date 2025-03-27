package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.dto.ChatRoomsDTO;
import com.wechat.bot.entity.dto.FriendDTO;
import com.wechat.bot.mapper.FriendMapper;
import com.wechat.bot.service.ChatRoomService;
import com.wechat.bot.service.FriendService;
import com.wechat.gewechat.service.ContactApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
            syncChatRooms(chatroomIds);
        }


    }


    private void syncChatRooms(List<String> chatroomIds) {

        chatRoomService.remove(null);
        List<List<String>> lists = splitIntoGroups(chatroomIds, 20);
        List<ChatRoomsDTO> chatRoomsDTOList = new ArrayList<>();
        for (List<String> list : lists) {
            JSONObject detailInfo = ContactApi.getDetailInfo(botConfig.getAppId(), list);
            if (detailInfo.getInteger("ret") != 200) {
                log.error("获取群聊列表失败");
                return;
            }
            JSONArray data2 = detailInfo.getJSONArray("data");
            for (Object o : data2) {
                JSONObject jsonObject = (JSONObject) o;
                ChatRoomsDTO chatRoomsDTO = ChatRoomsDTO.builder()
                        .chatRoomId(jsonObject.getString("userName"))
                        .nickName(jsonObject.getString("nickName"))
                        .pyInitial(jsonObject.getString("pyInitial"))
                        .quanPin(jsonObject.getString("quanPin"))
                        .sex(jsonObject.getString("sex"))
                        .remark(jsonObject.getString("remark"))
                        .remarkPyInitial(jsonObject.getString("remarkPyInitial"))
                        .remarkQuanPin(jsonObject.getString("remarkQuanPin"))
                        .signature(jsonObject.getString("signature"))
                        .build();
                chatRoomsDTOList.add(chatRoomsDTO);
            }
        }


        chatRoomService.saveOrUpdateBatch(chatRoomsDTOList);
    }

    private void syncFriends(List<String> friendIds) {

        this.remove(null);
        List<List<String>> lists = splitIntoGroups(friendIds, 20);
        List<FriendDTO> friends = new ArrayList<>();
        for (List<String> list : lists) {
            JSONObject response1 = ContactApi.getDetailInfo(botConfig.getAppId(), list);
            if (response1.getInteger("ret") != 200) {
                log.error("获取好友列表失败");
                return;
            }
            JSONArray data1 = response1.getJSONArray("data");

            List<FriendDTO> friendDTOList = data1.toList(FriendDTO.class);
            friends.addAll(friendDTOList);
        }

        this.saveOrUpdateBatch(friends);
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
