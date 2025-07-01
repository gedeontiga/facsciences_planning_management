package com.facsciences_planning_management.facsciences_planning_management.configs;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .featuresToDisable(

                        SerializationFeature.FAIL_ON_SELF_REFERENCES,

                        SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
