package com.example.javaasyncfeign.client;

import com.example.javaasyncfeign.domain.Example;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "demohttpfeign", url = "http://localhost:8080")
public interface DemoHttpFeign {

    @GetMapping("/api/teste2")
    Example request2();
}
