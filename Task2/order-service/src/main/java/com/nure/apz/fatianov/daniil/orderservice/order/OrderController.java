package com.nure.apz.fatianov.daniil.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //TODO зробити перевірку токенів для всіх функцій;

    @PostMapping("/add")
    public ResponseEntity<String> addOrder(@RequestBody OrderAddRequestBody requestBody) {
        try {
            orderService.addOrder(requestBody);
            return ResponseEntity.ok().body("Order added successfully");
        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add order: " + e.getMessage());
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<OrderModel>> getAll() {
        try {
            return ResponseEntity.ok(orderService.getAll());
        } catch (Exception e) {
            List<OrderModel> emptyList = new ArrayList<>();
            return new ResponseEntity<>(emptyList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/change")
    public ResponseEntity<String> changeOrder(@RequestBody OrderChangeRequestBody requestBody) {
        try {
            //TODO додати функції окремі для користувача та адміна для статусу CREATED or PROCESSED;
            orderService.changeOrder(requestBody);
            return ResponseEntity.ok().body("Order changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change order: " + e.getMessage());
        }
    }
}
