package com.microserviceapp.productmicroservice.repository;

import com.microserviceapp.productmicroservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
