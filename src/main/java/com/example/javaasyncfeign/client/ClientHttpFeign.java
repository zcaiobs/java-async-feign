package com.example.javaasyncfeign.client;

import com.example.javaasyncfeign.domain.Demo;
import com.example.javaasyncfeign.domain.Example;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "clienthttpfeign", url = "http://localhost:8080")
public interface ClientHttpFeign {

    @GetMapping("/api")
    ResponseEntity<String> getRequest();

    @GetMapping("/api/teste1")
    Demo request1();

    @GetMapping("/api/teste2")
    Example request2();

}
