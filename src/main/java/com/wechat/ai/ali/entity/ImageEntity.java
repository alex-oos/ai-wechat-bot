package com.wechat.ai.ali.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alex
 * @since 2025/3/12 17:47
 * <p></p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ImageEntity {

    private String image;

    private String text;

}
