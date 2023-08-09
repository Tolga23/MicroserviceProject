package com.microserviceapp.ordermicroservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced // This annotation is used to call the service using service name instead of IP address
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }
}   // WebClient.Builder is used to build the web client object
    // @LoadBalanced annotation is used to call the service using service name instead of IP address
