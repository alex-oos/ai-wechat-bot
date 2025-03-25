package com.wechat.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/3/25 23:07
 * <p>
 *
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SystemPromptEntity {

    String systemPrompt = "";

    String userName = "";

}
