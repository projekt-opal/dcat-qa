package com.marten.socialnetworkinterface.controllers;

import com.marten.socialnetworkinterface.configurations.SocialConfig;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RestController
public class TwitterWebhookController {

    @Autowired
    private SocialConfig socialConfig;

    @PostMapping("/webhook/twitter")
    public String reveiveEvent(@RequestBody String payload) {
        System.out.println(payload);
        return "test";
    }

    @GetMapping("/webhook/twitter")
    public String crcChallenge(@RequestParam String crc_token) {
        System.out.println("challenge");
        String solution = solveCRC(crc_token, socialConfig.getTwitterConsumerSecret()).toString();
        System.out.println(solution);
        return solution;
    }

    public String solveCRC(String crc_token, String consumer_secret) {
        System.out.println(consumer_secret + "\n" + crc_token);
        try {

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(consumer_secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(crc_token.getBytes(StandardCharsets.UTF_8)));

            String responseToken = "sha256=" + hash;

            return String.format("{ \"response_code\": 200, \"response_token\": \"%s\" }", responseToken);
            //return ResponseEntity.ok(new Response(200, responseToken));
        }
        catch (Exception e){
            System.out.println("Error");
        }
        return null;

    }

    private class Response {
        int response_code;
        String response_token;


        public Response(int response_code, String response_token) {
            this.response_code = response_code;
            this.response_token = response_token;
        }
    }

}
