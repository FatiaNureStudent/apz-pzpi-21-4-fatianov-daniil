package com.nure.apz.fatianov.daniil.orderservice.order;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

}
