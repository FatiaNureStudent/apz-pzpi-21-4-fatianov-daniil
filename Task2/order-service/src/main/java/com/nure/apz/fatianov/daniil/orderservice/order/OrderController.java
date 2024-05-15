package com.nure.apz.fatianov.daniil.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody OrderRequestBody requestBody) {
        try {
            orderService.addOrder(requestBody);
            return ResponseEntity.ok().body("Order added successfully");
        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add order: " + e.getMessage());
        }
    }
}
