package com.microserviceapp.productmicroservice.controller;

import com.microserviceapp.productmicroservice.dto.ProductRequestDto;
import com.microserviceapp.productmicroservice.dto.ProductResponseDto;
import com.microserviceapp.productmicroservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequestDto productRequestDto) {
        productService.createProduct(productRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDto> getAllProducts(){
        return productService.getAllProducts();
    }
}
