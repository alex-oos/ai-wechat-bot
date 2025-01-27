package com.wechat.fatory;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Alex
 * @since 2025/1/27 11:19
 * <p></p>
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @NotNull
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties properties = factory.getObject();
        if (properties != null) {
            return new PropertiesPropertySource(
                (name != null) ? name : Objects.requireNonNull(resource.getResource().getFilename()),
                properties
            );
        }
        return null;
    }
}
