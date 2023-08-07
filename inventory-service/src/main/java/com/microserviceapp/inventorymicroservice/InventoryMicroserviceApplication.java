package com.microserviceapp.inventorymicroservice;

import com.microserviceapp.inventorymicroservice.model.Inventory;
import com.microserviceapp.inventorymicroservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryMicroserviceApplication.class, args);
	}

	// load some data to test the application
	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return (args) -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iphone14");
			inventory.setQuantity(100);


			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("iphone15");
			inventory2.setQuantity(0);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory2);
		};
	}

}
