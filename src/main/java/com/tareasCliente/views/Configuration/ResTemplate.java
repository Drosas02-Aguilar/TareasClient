package com.tareasCliente.views.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ResTemplate {

    @Bean
    public RestTemplate restTemplate(){
    
        return new RestTemplate();
    }



    
}
