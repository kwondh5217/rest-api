package com.example.restapi.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

//@TestConfiguration
public class RestDocsConfiguration {

//    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer(){
        return configurer -> configurer.operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());

    }
}
