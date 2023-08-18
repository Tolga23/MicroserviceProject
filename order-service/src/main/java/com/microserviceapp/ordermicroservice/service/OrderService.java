package com.microserviceapp.ordermicroservice.service;

import brave.Span;
import brave.Tracer;
import com.microserviceapp.ordermicroservice.dto.InventoryResponseDto;
import com.microserviceapp.ordermicroservice.dto.OrderLineItemsDto;
import com.microserviceapp.ordermicroservice.dto.OrderRequestDto;
import com.microserviceapp.ordermicroservice.enums.OrderResponseMessage;
import com.microserviceapp.ordermicroservice.event.OrderPlacedEvent;
import com.microserviceapp.ordermicroservice.model.Order;
import com.microserviceapp.ordermicroservice.model.OrderLineItems;
import com.microserviceapp.ordermicroservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final Tracer tracer;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public String placedOrder(OrderRequestDto orderRequestDto) {
        List<OrderLineItems> orderLineItems = getOrderLineItems(orderRequestDto);

        Order order = createNewOrder(orderLineItems);
        
        List<String> skuCodesList = getSkuCodesList(order);

        Span callingInventoryService = tracer.nextSpan().name("Before calling inventory service");

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(callingInventoryService.start())) {
            InventoryResponseDto[] inventoryResponseDtos = callToInventoryService(skuCodesList);

            boolean allProductsInStock = checkAllProductsInStock(inventoryResponseDtos);

            if (allProductsInStock) {
                return saveOrder(order);
            } else {
                throw new IllegalArgumentException(OrderResponseMessage.PRODUCT_OUT_OF_STOCK.getMessage());
            }
        } catch (Exception e) {
            callingInventoryService.error(e);
            throw e;
        } finally {
            callingInventoryService.finish();
        }
    }

    private String saveOrder(Order order) {
        orderRepository.save(order);
        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
        return OrderResponseMessage.ORDER_PLACED_SUCCESSFULLY.getMessage();
    }

    private Order createNewOrder(List<OrderLineItems> orderLineItems) {
        Order order = new Order();
        order.setOrderNumber(getRandomOrderNumber());
        order.setOrderLineItemsList(orderLineItems);
        return order;
    }

    private String getRandomOrderNumber() {
        return UUID.randomUUID().toString();
    }

    private List<OrderLineItems> getOrderLineItems(OrderRequestDto orderRequestDto) {
        List<OrderLineItems> orderLineItems = orderRequestDto.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
                .toList();
        return orderLineItems;
    }

    private boolean checkAllProductsInStock(InventoryResponseDto[] inventoryResponseDtos) {
        boolean allProductsInStock = Arrays.stream(inventoryResponseDtos)
                .allMatch(InventoryResponseDto::isInStock);
        return allProductsInStock;
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
