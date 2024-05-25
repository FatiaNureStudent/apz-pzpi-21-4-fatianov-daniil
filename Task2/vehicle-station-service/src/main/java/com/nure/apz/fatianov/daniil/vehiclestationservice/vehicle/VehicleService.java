package com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle;

import com.nure.apz.fatianov.daniil.vehiclestationservice.station.Station;
import com.nure.apz.fatianov.daniil.vehiclestationservice.station.StationRepository;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.request.VehicleAddRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.request.VehicleChangeRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.response.VehicleGetAllResponseEntity;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.response.objects.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private static final String orderServiceUrl = "http://localhost:8083";

    private final VehicleRepository vehicleRepository;

    private final StationRepository stationRepository;
    private final RestTemplateBuilder restTemplateBuilder;

    public void addVehicle(VehicleAddRequest requestBody) {
        Optional<Station> optionalStation = stationRepository.findByNumber(requestBody.getStationNumber());

        if(optionalStation.isEmpty()) {
            throw new IllegalStateException("Station with number: "
                    + requestBody.getStationNumber() + "does not exist");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setNumber(requestBody.getNumber());
        vehicle.setLiftingCapacity(requestBody.getLiftingCapacity());
        vehicle.setFlightDistance(requestBody.getFlightDistance());
        vehicle.setStatus(Status.AVAILABLE);
        vehicle.setStation(optionalStation.get());

        vehicleRepository.saveAndFlush(vehicle);


    }

    public List<VehicleGetAllResponseEntity> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleGetAllResponseEntity> vehicleGetAllResponseEntities = new ArrayList<>();
        for(Vehicle vehicle : vehicles) {
            VehicleGetAllResponseEntity vehicleGetAllResponseEntity = new VehicleGetAllResponseEntity();
            vehicleGetAllResponseEntity.setId(vehicle.getId());
            vehicleGetAllResponseEntity.setNumber(vehicle.getNumber());
            vehicleGetAllResponseEntity.setLiftingCapacity(vehicle.getLiftingCapacity());
            vehicleGetAllResponseEntity.setFlightDistance(vehicle.getFlightDistance());
            vehicleGetAllResponseEntity.setStatus(vehicle.getStatus());
            vehicleGetAllResponseEntity.setStationNumber(vehicle.getStation().getNumber());

            HttpEntity<Integer> orderHttpEntity = new HttpEntity<>(vehicle.getId());
            String urlToOrderService = orderServiceUrl.concat("/get-order-for-vehicle");
            try{
                RestTemplate restTemplate = restTemplateBuilder.build();
                ResponseEntity<Order> responseEntity = restTemplate.postForEntity(urlToOrderService, orderHttpEntity, Order.class);

                if(responseEntity.getStatusCode().is2xxSuccessful()) {
                    vehicleGetAllResponseEntity.setOrder(responseEntity.getBody());
                }else{
                    vehicleGetAllResponseEntity.setOrder(null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            vehicleGetAllResponseEntities.add(vehicleGetAllResponseEntity);

        }

        return vehicleGetAllResponseEntities;
    }

    public String getVehicleNumber(Integer id) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with id: " + id + " does not exist");
        }

        return optionalVehicle.get().getNumber();
    }

    public void changeVehicle(VehicleChangeRequest requestBody) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(requestBody.getId());

        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with id: " + requestBody.getId() + " does not exist");
        }

        Vehicle vehicle = optionalVehicle.get();
        vehicle.setNumber(requestBody.getNumber());
        vehicle.setLiftingCapacity(requestBody.getLiftingCapacity());
        vehicle.setFlightDistance(requestBody.getFlightDistance());

        vehicleRepository.saveAndFlush(vehicle);
    }

    public void getVehicleReady(String number) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByNumber(number);
        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with number: " + number + " does not exist");
        }

        Vehicle vehicle = optionalVehicle.get();
        vehicle.setStatus(Status.READY);
        vehicleRepository.saveAndFlush(vehicle);
    }

    public void deleteVehicle(String number) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByNumber(number);
        if(optionalVehicle.isEmpty()) {
            throw new IllegalStateException("Vehicle with number: " + number + " does not exist");
        }

        Vehicle vehicle = optionalVehicle.get();

        HttpEntity<Integer> orderHttpEntity = new HttpEntity<>(vehicle.getId());
        String urlToOrderService = orderServiceUrl.concat("/get-order-for-vehicle");
        try{
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<Order> responseEntity = restTemplate.postForEntity(urlToOrderService, orderHttpEntity, Order.class);

            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                Order order = responseEntity.getBody();
                if(Order.orderHasDetails(order)) {
                    throw new IllegalStateException("Vehicle with number: " + number + " has order");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        vehicleRepository.delete(vehicle);
    }
}
