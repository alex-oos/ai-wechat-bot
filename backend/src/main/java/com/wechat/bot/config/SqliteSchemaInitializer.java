package com.wechat.bot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

@Slf4j
@Configuration
@Profile("sqlite")
public class SqliteSchemaInitializer {

    private static final String SQLITE_SCHEMA_PATH = "classpath:db/sqlite/sqlite.sql";

    @Bean("sqliteSchemaInit")
    public InitializingBean sqliteSchemaInit(DataSource dataSource, ResourceLoader resourceLoader) {
        return () -> {
            ensureDataDirectory();
            Resource schema = resourceLoader.getResource(SQLITE_SCHEMA_PATH);
            if (!schema.exists()) {
                log.warn("SQLite schema not found at {}", SQLITE_SCHEMA_PATH);
                return;
            }

            String sql;
            try (var in = schema.getInputStream()) {
                sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            String cleaned = sql.replaceAll("(?m)^\\s*--.*$", "").trim();
            if (cleaned.isEmpty()) {
                log.warn("SQLite schema is empty, skipping initialization.");
                return;
            }

            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                for (String raw : cleaned.split(";")) {
                    String ddl = raw.trim();
                    if (!ddl.isEmpty()) {
                        try {
                            statement.execute(ddl);
                        } catch (Exception ex) {
                            log.warn("SQLite schema statement failed, skipping: {}", ddl, ex);
                        }
                    }
                }
            }

            log.info("SQLite schema initialization completed.");
        };
    }

    private void ensureDataDirectory() {
        try {
            Path dataDir = Path.of("data");
            Files.createDirectories(dataDir);
        } catch (Exception ex) {
            log.warn("Failed to ensure SQLite data directory exists.", ex);
        }
    }
}
