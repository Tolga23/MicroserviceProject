package com.microserviceapp.ordermicroservice.enums;

public enum OrderResponseMessage {

    ORDER_PLACED_SUCCESSFULLY("Order placed successfully"),
    ORDER_SERVICE_DOWN("Order service is down, please try again later"),
    PRODUCT_OUT_OF_STOCK("Product is out of stock");

    private String message;
    OrderResponseMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    @Override
    public String toString(){
        return message;
    }

}
