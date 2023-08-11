package com.microserviceapp.ordermicroservice.controller;

import com.microserviceapp.ordermicroservice.dto.OrderRequestDto;
import com.microserviceapp.ordermicroservice.enums.OrderResponseMessage;
import com.microserviceapp.ordermicroservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CircuitBreaker annotation is used to handle the exception thrown by the service
    // fallbackMethod is used to call the fallback method if the service throws an exception
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequestDto orderRequestDto){
        return CompletableFuture.supplyAsync(() ->orderService.placedOrder(orderRequestDto));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequestDto orderRequestDto, RuntimeException e){
        return CompletableFuture.supplyAsync(() -> OrderResponseMessage.ORDER_SERVICE_DOWN.getMessage());
    }
}
