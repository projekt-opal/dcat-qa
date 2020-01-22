package com.marten.socialnetworkinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class SocialNetworkInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkInterfaceApplication.class, args);
    }

}
