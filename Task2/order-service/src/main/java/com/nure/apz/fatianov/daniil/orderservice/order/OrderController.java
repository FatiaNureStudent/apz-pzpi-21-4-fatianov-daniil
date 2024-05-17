package com.nure.apz.fatianov.daniil.orderservice.order;

import com.nure.apz.fatianov.daniil.orderservice.model.OrderModel;
import com.nure.apz.fatianov.daniil.orderservice.request.OrderAddRequestBody;
import com.nure.apz.fatianov.daniil.orderservice.request.OrderChangeRequestBody;
import com.nure.apz.fatianov.daniil.orderservice.request.OrderProcessRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<String> addOrder(
            @RequestBody OrderAddRequestBody requestBody,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        try {
            orderService.addOrder(requestBody, authorizationHeader);
            return ResponseEntity.ok().body("Order added successfully");
        } catch (Exception e) {
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Failed to add order: " + e.getMessage());
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

    //TODO додати токен авторизації для отримання користувача
    // та відпрвлення йому повідомленя через сервіс повідомлень
    @PutMapping("/change")
    public ResponseEntity<String> changeOrder(
            @RequestBody OrderChangeRequestBody requestBody
    ) {
        try {
            //TODO додати функції окремі для користувача та адміна для статусу CREATED or PROCESSED;
            orderService.changeOrder(requestBody);
            return ResponseEntity.ok().body("Order processed successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process order: " + e.getMessage());
        }
    }

    //TODO додати токен авторизації для отримання користувача
    // та відпрвлення йому повідомленя через сервіс повідомлень
    @PutMapping("/process")
    public ResponseEntity<String> processOrder(
            @RequestBody OrderProcessRequestBody requestBody
    ) {
        try {
            orderService.processOrder(requestBody);
            return ResponseEntity.ok().body("Order changed successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to change order: " + e.getMessage());
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<String> updateOrderStatus(
            @RequestParam String id,
            @RequestParam Status status
    ) {
        try {
            orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok().body("Order status updated successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update order status: " + e.getMessage());
        }
    }

    @GetMapping("/statuses")
    public ResponseEntity<Status[]> getAllStatuses() {
        Status[] allStatuses = Status.values();
        return ResponseEntity.ok(allStatuses);
    }
}
