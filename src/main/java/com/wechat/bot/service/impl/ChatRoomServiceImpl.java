package com.wechat.bot.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.bot.entity.BotConfig;
import com.wechat.bot.entity.dto.ChatRoomsDTO;
import com.wechat.bot.mapper.ChatRoomMapper;
import com.wechat.bot.service.ChatRoomService;
import com.wechat.gewechat.service.ContactApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alex
 * @since 2025/1/27 22:19
 * <p></p>
 */
@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomsDTO> implements ChatRoomService {

    @Autowired
    private BotConfig botConfig;

    @Override
    public ChatRoomsDTO getChatRoomByRoomId(String roomId) {
        // 1. 先用roomid 在数据库中查询看看是否存在，
        //2. 如果存在直接返回，如果不存在，再调用接口获取，然后保存到数据库中，再返回
        LambdaQueryWrapper<ChatRoomsDTO> queryWrapper = new QueryWrapper<ChatRoomsDTO>().lambda();
        queryWrapper.eq(ChatRoomsDTO::getChatRoomId, roomId);
        ChatRoomsDTO chatRoomsDTO = this.getOne(queryWrapper);
        if (chatRoomsDTO != null) {
            return chatRoomsDTO;
        }
        List<ChatRoomsDTO> chatRoomsDTOS = requestChatRoomInfo(Collections.singletonList(roomId));

        this.saveOrUpdateBatch(chatRoomsDTOS);
        return chatRoomsDTOS.stream().findFirst().orElseThrow(() -> new RuntimeException("获取群聊列表失败"));

    }

    @Override
    public void syncChatRooms(List<String> chatroomIds) {

        this.remove(null);
        List<List<String>> lists = splitIntoGroups(chatroomIds, 20);
        List<ChatRoomsDTO> chatRoomsDTOList = new ArrayList<>();
        for (List<String> list : lists) {

            List<ChatRoomsDTO> chatRoomsDTOS = requestChatRoomInfo(list);
            chatRoomsDTOList.addAll(chatRoomsDTOS);
        }


        this.saveOrUpdateBatch(chatRoomsDTOList);
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

    @Transactional(rollbackFor = Exception.class)
    public List<ChatRoomsDTO> requestChatRoomInfo(List<String> chatroomIds) {

        List<ChatRoomsDTO> chatRoomsDTOList = new ArrayList<>();


        JSONObject detailInfo = ContactApi.getDetailInfo(botConfig.getAppId(), chatroomIds);
        if (detailInfo.getInteger("ret") != 200) {
            log.error("获取群聊列表失败");
            throw new RuntimeException("获取群聊列表失败");

        }
        JSONArray data2 = detailInfo.getJSONArray("data");
        for (Object o : data2) {
            JSONObject jsonObject = (JSONObject) o;
            ChatRoomsDTO chatRoomsDTO = ChatRoomsDTO.builder().chatRoomId(jsonObject.getString("userName")).nickName(jsonObject.getString("nickName")).pyInitial(jsonObject.getString("pyInitial")).quanPin(jsonObject.getString("quanPin")).sex(jsonObject.getString("sex")).remark(jsonObject.getString("remark")).remarkPyInitial(jsonObject.getString("remarkPyInitial")).remarkQuanPin(jsonObject.getString("remarkQuanPin")).signature(jsonObject.getString("signature")).build();
            chatRoomsDTOList.add(chatRoomsDTO);
        }

        return chatRoomsDTOList;
    }


}
