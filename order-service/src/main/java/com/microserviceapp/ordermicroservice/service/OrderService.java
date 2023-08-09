package com.microserviceapp.ordermicroservice.service;

import com.microserviceapp.ordermicroservice.dto.InventoryResponseDto;
import com.microserviceapp.ordermicroservice.dto.OrderLineItemsDto;
import com.microserviceapp.ordermicroservice.dto.OrderRequestDto;
import com.microserviceapp.ordermicroservice.model.Order;
import com.microserviceapp.ordermicroservice.model.OrderLineItems;
import com.microserviceapp.ordermicroservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placedOrder(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequestDto.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodesList = getSkuCodesList(order);

        InventoryResponseDto[] inventoryResponseDtos = callToInventoryService(skuCodesList);

        boolean allProductsInStock = Arrays.stream(inventoryResponseDtos)
                .allMatch(InventoryResponseDto::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        }else {
            throw new IllegalArgumentException("Product is out of stock");
        }

    }

    private InventoryResponseDto[] callToInventoryService(List<String> skuCodes) {
        // Call Inventory service, and place order if product is in stock
        // we used inventory-service instead of localhost because we used @LoadBalanced annotation in WebClientConfig
        // @LoadBalanced annotation is used to call the service using service name instead of IP address
        InventoryResponseDto[] inventoryResponseDtos = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponseDto[].class)
                .block();

      

        return inventoryResponseDtos;
    }

    private List<String> getSkuCodesList(Order order) {
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        return skuCodes;
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }
}
