package com.wechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Alex
 * @since 2025/1/24 15:18
 * <p></p>
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 定位到项目文件夹下的data/images文件夹，作为个人静态资源目标
    public static final String IMAGE_PATH = System.getProperty("user.dir") + "/data/";


    // 参考教程：https://cloud.tencent.com/developer/article/1892774
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //静态资源映射,
        // static 目录是默认，不需要配置
        //registry.addResourceHandler("/static/**")
        //        .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/data/**")
                .addResourceLocations("file:" + IMAGE_PATH);


    }

}
