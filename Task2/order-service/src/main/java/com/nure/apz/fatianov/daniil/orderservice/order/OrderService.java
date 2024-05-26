package com.nure.apz.fatianov.daniil.orderservice.order;

import com.nure.apz.fatianov.daniil.orderservice.request.OrderAddRequestBody;
import com.nure.apz.fatianov.daniil.orderservice.request.OrderChangeRequestBody;
import com.nure.apz.fatianov.daniil.orderservice.request.OrderProcessRequestBody;
import com.nure.apz.fatianov.daniil.orderservice.response.OrderAdminGetResponseEntity;
import com.nure.apz.fatianov.daniil.orderservice.response.OrderUserGetResponse;
import com.nure.apz.fatianov.daniil.orderservice.response.OrderVehicleGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplateBuilder restTemplateBuilder;

    private static final String shippingServiceUrl = "http://localhost:8086";
    private static final String userServiceUrl = "http://localhost:8082";
    private static final String vehicleStationServiceUrl = "http://localhost:8084";

    public String generatePassword() {
        Random random = new Random();

        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            passwordBuilder.append(digit);
        }

        return passwordBuilder.toString();
    }

    public void addOrder(OrderAddRequestBody requestBody, String authorizationHeader) {
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

        //TODO зробити запит на отримання айдішника користувача по токену на сервіс аутентифікації

        Order newOrder = new Order(
                9999,                               // TEST
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

    public List<OrderAdminGetResponseEntity> getAll() {
        List<Order> orders = orderRepository.findAll();

        List<OrderAdminGetResponseEntity> responseEntities = new ArrayList<>();
        for(Order order : orders) {
            OrderAdminGetResponseEntity orderAdminGetResponse = new OrderAdminGetResponseEntity();
            orderAdminGetResponse.setId(order.getId());
            orderAdminGetResponse.setNumber(order.getNumber());
            orderAdminGetResponse.setReceiptCode(order.getReceiptCode());
            orderAdminGetResponse.setStatus(order.getStatus());
            orderAdminGetResponse.setCreationDate(order.getCreationDate());

            HttpEntity<Integer> userHttpEntity = new HttpEntity<>(order.getUserId());
            String urlToUserService = userServiceUrl.concat("/get-user-email");
            try{
                RestTemplate restTemplate = restTemplateBuilder.build();
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlToUserService, userHttpEntity, String.class);

                if(responseEntity.getStatusCode().is2xxSuccessful()) {
                    orderAdminGetResponse.setUserEmail(responseEntity.getBody());
                }else{
                    throw new IllegalStateException("User with id "
                            + order.getUserId() + " can not be found");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            HttpEntity<Integer> vehicleHttpEntity = new HttpEntity<>(order.getVehicleId());
            String urlToVehicleService = vehicleStationServiceUrl.concat("/get-vehicle-number");
            try{
                RestTemplate restTemplate = restTemplateBuilder.build();
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlToVehicleService, vehicleHttpEntity, String.class);

                if(responseEntity.getStatusCode().is2xxSuccessful()) {
                    orderAdminGetResponse.setVehicleNumber(responseEntity.getBody());
                }else{
                    throw new IllegalStateException("Vehicle with id "
                            + order.getVehicleId() + " can not be found");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String urlToStationService = vehicleStationServiceUrl.concat("/get-station-number");

            HttpEntity<Integer> DepartureStationHttpEntity = new HttpEntity<>(order.getDepartureStationId());
            try{
                RestTemplate restTemplate = restTemplateBuilder.build();
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlToStationService, DepartureStationHttpEntity, String.class);

                if(responseEntity.getStatusCode().is2xxSuccessful()) {
                    orderAdminGetResponse.setDepartureStationNumber(responseEntity.getBody());
                }else{
                    throw new IllegalStateException("Station with id "
                            + order.getDepartureStationId() + " can not be found");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            HttpEntity<Integer> ArrivalStationHttpEntity = new HttpEntity<>(order.getArrivalStationId());
            try{
                RestTemplate restTemplate = restTemplateBuilder.build();
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlToStationService, ArrivalStationHttpEntity, String.class);

                if(responseEntity.getStatusCode().is2xxSuccessful()) {
                    orderAdminGetResponse.setArrivalStationNumber(responseEntity.getBody());
                }else{
                    throw new IllegalStateException("Station with id "
                            + order.getDepartureStationId() + " can not be found");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            responseEntities.add(orderAdminGetResponse);
        }
        return responseEntities;
    }

    public void changeOrder(
            OrderChangeRequestBody requestBody
    ) {
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

        newOrder.setArrivalStationId(requestBody.getArrivalStationId());
        newOrder.setItems(requestBody.getItems());

        //TODO додати повідомлення про зміну замовлення;

        orderRepository.save(newOrder);
    }

    public void processOrder(
            OrderProcessRequestBody requestBody
    ) {
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

        newOrder.setVehicleId(requestBody.getVehicleId());
        newOrder.setDepartureStationId(requestBody.getDepartureStationId());
        newOrder.setItems(requestBody.getItems());
        newOrder.setStatus(Status.PROCESSED);

        //TODO додати повідомлення про зміну замовлення;

        orderRepository.save(newOrder);
    }

    public void updateOrderStatus(String id, Status status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isEmpty()) {
            throw new IllegalStateException("Order with id: " + id + " does not exist");
        }

        Order order = optionalOrder.get();
        order.setStatus(status);
        orderRepository.save(order);
    }

    public OrderVehicleGetResponse getOrderForVehicle(Integer id) {
        Optional<Order> optionalOrder = orderRepository.findByVehicleIdAndStatusLike(id, Status.PROCESSED);
        if(optionalOrder.isEmpty()) {
            throw new IllegalStateException("Order for vehicle: " + id
                    + " and status: " + Status.PROCESSED + " does not exist");
        }

        Order order = optionalOrder.get();
        OrderVehicleGetResponse orderVehicleGetResponse = new OrderVehicleGetResponse();
        orderVehicleGetResponse.setNumber(order.getNumber());
        orderVehicleGetResponse.setItems(order.getItems());

        String urlToStationService = vehicleStationServiceUrl.concat("/get-station-number");

        HttpEntity<Integer> arrivalStationHttpEntity = new HttpEntity<>(order.getArrivalStationId());
        try{
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlToStationService, arrivalStationHttpEntity, String.class);

            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                orderVehicleGetResponse.setArrivalStationNumber(responseEntity.getBody());
            }else{
                throw new IllegalStateException("Station with id "
                        + order.getDepartureStationId() + " can not be found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return orderVehicleGetResponse;
    }

    public List<OrderUserGetResponse>  getOrdersForUser(String token) {
        List<OrderUserGetResponse> orderUserGetResponses = new ArrayList<>();

        //TODO перевірка ролі користувача
        String urlToUserService = userServiceUrl.concat("/get-userId");

        Integer userId;
        String arrivalStationNumber;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity= new HttpEntity<>(headers);
        try{
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<Integer> responseEntity = restTemplate.exchange(urlToUserService, HttpMethod.GET, entity, Integer.class);

            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                userId = responseEntity.getBody();

            }else{
                throw new IllegalStateException("Something went wrong");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Optional<List<Order>> optionalOrder = orderRepository.findAllByUserId(userId);
        if(optionalOrder.isEmpty()) {
            throw new IllegalStateException("Orders for user: " + token + " does not exist");
        }

        List<Order> orders = optionalOrder.get();
        for(Order order : orders) {
            OrderUserGetResponse orderUserGetResponse = new OrderUserGetResponse();
            orderUserGetResponse.setNumber(order.getNumber());
            orderUserGetResponse.setItems(order.getItems());
            orderUserGetResponse.setReceiptCode(order.getReceiptCode());
            orderUserGetResponse.setCreationDate(order.getCreationDate());
            orderUserGetResponse.setStatus(order.getStatus());

            String urlToStationService = vehicleStationServiceUrl.concat("/get-station-number");

            HttpEntity<Integer> arrivalStationHttpEntity = new HttpEntity<>(order.getArrivalStationId());
            try{
                RestTemplate restTemplate = restTemplateBuilder.build();
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlToStationService, arrivalStationHttpEntity, String.class);

                if(responseEntity.getStatusCode().is2xxSuccessful()) {
                    orderUserGetResponse.setArrivalStationNumber(responseEntity.getBody());
                }else{
                    throw new IllegalStateException("Station with id "
                            + order.getDepartureStationId() + " can not be found");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            orderUserGetResponses.add(orderUserGetResponse);
        }

        return orderUserGetResponses;

    }
}
