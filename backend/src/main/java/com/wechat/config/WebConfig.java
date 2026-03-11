package com.wechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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

    /**
     * 解决全局跨域，
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加映射路径
        registry.addMapping("/**")
                // 放行哪些原始域
                .allowedOriginPatterns("*")
                // 是否发送Cookie信息
                .allowCredentials(true)
                // 放行哪些原始域(请求方式)
                .allowedMethods("*")
                // 放行哪些原始域(头部信息)
                .allowedHeaders("*")
                // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
                .exposedHeaders("access-control-allow-headers",
                        "access-control-allow-methods",
                        "access-control-allow-origin",
                        "access-control-max-age",
                        "X-Frame-Options");
    }

}
