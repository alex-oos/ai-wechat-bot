package com.wechat.ai.langchain4j.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.INTEGER;
import static org.mapdb.Serializer.STRING;

/**
 * @author Alex
 * @since 2025/4/22 15:05
 * <p>
 * 自定义持久化聊天记录
 * </p>
 */
@Service
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final DB db = DBMaker.fileDB("./chat-memory.db").transactionEnable().make();

    private final Map<Integer, String> map = db.hashMap("messages", INTEGER, STRING).createOrOpen();
    // 使用方式
    //ChatMemory chatMemory = MessageWindowChatMemory.builder().id("12345").maxMessages(10).chatMemoryStore(new PersistentChatMemoryStore()).build();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // TODO: Implement getting all messages from the persistent store by memory ID.
        // ChatMessageDeserializer.messageFromJson(String) and
        // ChatMessageDeserializer.messagesFromJson(String) helper methods can be used to
        // easily deserialize chat messages from JSON.
        String json = map.get((int) memoryId);
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // TODO: Implement updating all messages in the persistent store by memory ID.
        String json = messagesToJson(messages);
        map.put((int) memoryId, json);
        db.commit();
    }

    @Override
    public void deleteMessages(Object memoryId) {

        map.remove((int) memoryId);
        db.commit();
    }

}
