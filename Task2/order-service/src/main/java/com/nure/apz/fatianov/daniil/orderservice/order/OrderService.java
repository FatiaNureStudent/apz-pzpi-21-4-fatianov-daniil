package com.nure.apz.fatianov.daniil.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void addOrder(OrderRequestBody requestBody) {
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

    public String generatePassword() {
        Random random = new Random();

        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            passwordBuilder.append(digit);
        }

        return passwordBuilder.toString();
    }
}
