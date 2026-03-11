package com.wechat.knowledgebase.ragflow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/4/16 21:37
 * <p></p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReplyEntity {

    private String answer;

    private String sessionId;

}
