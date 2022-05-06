package com.example.javaasyncfeign.controller;

import com.example.javaasyncfeign.client.ClientHttpFeign;
import com.example.javaasyncfeign.client.DemoHttpFeign;
import com.example.javaasyncfeign.domain.Demo;
import com.example.javaasyncfeign.domain.Example;
import com.example.javaasyncfeign.domain.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@RequestMapping("/api")
@RestController
@Slf4j
public class ClientController {

    @Autowired
    ClientHttpFeign clientHttpFeign;

    @Autowired
    DemoHttpFeign demoHttpFeign;

    @GetMapping("/teste")
    ResponseEntity<?> teste() {
        try {
            var request = new ArrayList<CompletableFuture<?>>();

            request.add(step(clientHttpFeign::request1));
            request.add(step(demoHttpFeign::request2));

            var resp = Response.builder().build();

            request.stream()
                    .map(CompletableFuture::join)
                    .peek(System.out::println)
                    .forEach(result -> {
                        if (result.getClass().isAssignableFrom(Demo.class)) {
                            var data = (Demo) result;
                            resp.setText(data.getValue());
                        } else {
                            var data = (Example) result;
                            resp.setNumber(data.getValue());
                        }
                    });

            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping("/client")
    ResponseEntity<?> execute() {
        try {
            var requests = new ArrayList<CompletableFuture<?>>();
            for(int count = 0; count < 1000; count ++) {
                requests.add(step(clientHttpFeign::getRequest));
            }

            var result = requests.stream()
            .map(CompletableFuture::join)
            .map(o -> ((ResponseEntity<String>) o))
            .map(ResponseEntity::getBody)
            .toList();

            result.forEach(task -> log.info("{}", task));

            if(!result.isEmpty()) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.unprocessableEntity().body("Could not perform this task");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Server is unavaible");
        }
    }

    CompletableFuture<?> step(Supplier<?> request) {
        return CompletableFuture.supplyAsync(request);
    }
}
