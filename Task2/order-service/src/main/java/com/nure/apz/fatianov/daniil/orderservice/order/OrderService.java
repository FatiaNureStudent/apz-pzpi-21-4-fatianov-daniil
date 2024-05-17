package com.nure.apz.fatianov.daniil.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public String generatePassword() {
        Random random = new Random();

        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            passwordBuilder.append(digit);
        }

        return passwordBuilder.toString();
    }

    public void addOrder(OrderAddRequestBody requestBody) {
        //Todo Зробити генерацію номеру за допомогою шифрування
        // обробника,отримувача, точок відправлення та отримання та країни,
        // на прикладі номеру замовлень нової пошти

        Optional<Order> optionalOrder =
                orderRepository.findByNumberAndStatusNotLike(
                        requestBody.getNumber(),
                        Status.RECEIVED
                );

        if(optionalOrder.isPresent()) {
            throw new IllegalStateException("Order with number "
                    + requestBody.getNumber() + " already exists and has status: "
                    + optionalOrder.get().getStatus());
        }

        Order newOrder = new Order(
                requestBody.getUserId(),
                requestBody.getVehicleId(),
                requestBody.getDepartureStationId(),
                requestBody.getArrivalStationId(),
                requestBody.getNumber(),
                requestBody.getItems()

        );

        newOrder.setStatus(Status.CREATED);
        newOrder.setReceiptCode(generatePassword());
        newOrder.setCreationDate(requestBody
                .getCreationDate()
                .toLocalDateTime()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime()
        );

        orderRepository.save(newOrder);
    }

    public List<OrderModel> getAll() {
        List<Order> orders = orderRepository.findAll();
        List<OrderModel> orderModels = new ArrayList<>();
        for (Order order : orders) {
            orderModels.add(
                    OrderModel.toModel(order)
            );
        }

        return orderModels;
    }

    public void changeOrder(OrderChangeRequestBody requestBody) {
        Optional<Order> optionalOrder = orderRepository.findById(requestBody.getId());
        if(optionalOrder.isEmpty()) {
            throw new IllegalStateException("Order with id " + requestBody.getId() + " does not exist");
        }

        Order newOrder = optionalOrder.get();

        if(newOrder.getStatus() == Status.RECEIVED ||
                newOrder.getStatus() == Status.DENIED ||
                newOrder.getStatus() == Status.SENT) {
            throw new IllegalStateException("Order with id " +
                    requestBody.getId() + " already has" + newOrder.getStatus());
        }

        newOrder.setUserId(requestBody.getUserId());
        newOrder.setVehicleId(requestBody.getVehicleId());
        newOrder.setDepartureStationId(requestBody.getDepartureStationId());
        newOrder.setArrivalStationId(requestBody.getArrivalStationId());
        newOrder.setItems(requestBody.getItems());
        newOrder.setStatus(Status.CREATED);

        //TODO додати повідомлення про зміну замовлення;

        orderRepository.save(newOrder);
    }
}