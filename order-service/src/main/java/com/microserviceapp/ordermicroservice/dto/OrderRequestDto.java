package com.microserviceapp.ordermicroservice.dto;

import com.microserviceapp.ordermicroservice.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
