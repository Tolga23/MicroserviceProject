package com.microserviceapp.ordermicroservice.controller;

import com.microserviceapp.ordermicroservice.dto.OrderRequestDto;
import com.microserviceapp.ordermicroservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequestDto orderRequestDto){
        orderService.placedOrder(orderRequestDto);
        return "Order placed successfully";
    }
}
