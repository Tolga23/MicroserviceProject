package com.microserviceapp.productmicroservice.service;

import com.microserviceapp.productmicroservice.dto.ProductRequestDto;
import com.microserviceapp.productmicroservice.dto.ProductResponseDto;
import com.microserviceapp.productmicroservice.model.Product;
import com.microserviceapp.productmicroservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequestDto productRequestDto) {
        Product product = Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductResponseDto> productResponseDtoList = products.stream().map(this::mapToProductResponseDto).toList();

        return productResponseDtoList;
    }

    private ProductResponseDto mapToProductResponseDto(Product product) {
        ProductResponseDto productResponseDto = ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();

        return productResponseDto;
    }
}
