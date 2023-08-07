package com.microserviceapp.inventorymicroservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_INVENTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String skuCode;
    private Integer quantity;
}
