package com.nure.apz.fatianov.daniil.vehiclestationservice.station;

import com.nure.apz.fatianov.daniil.vehiclestationservice.station.request.StationAddRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.station.request.StationChangeRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.station.response.StationGetAllResponseEntity;
import com.nure.apz.fatianov.daniil.vehiclestationservice.station.response.objects.VehicleEntityForStation;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.Vehicle;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationService {

    private final VehicleRepository vehicleRepository;

    private final StationRepository stationRepository;

    public void addStation(StationAddRequest requestBody) {
        Optional<Station> optionalStation = stationRepository.findByNumber(requestBody.getNumber());
        if(optionalStation.isPresent()) {
            throw new IllegalStateException("Station with number: " + requestBody.getNumber() + " already exist");
        }

        Station station = new Station();
        station.setNumber(requestBody.getNumber());
        station.setDescription(requestBody.getDescription());
        station.setLatitude(requestBody.getLatitude());
        station.setLongitude(requestBody.getLongitude());
        station.setAltitude(requestBody.getAltitude());
        station.setType(requestBody.getType());
        stationRepository.saveAndFlush(station);
    }

    public List<StationGetAllResponseEntity> getAllStations() {
        List<Station> stations = stationRepository.findAll();
        List<StationGetAllResponseEntity> stationGetAllResponseEntities = new ArrayList<>();
        for(Station station : stations) {
            StationGetAllResponseEntity stationGetAllResponseEntity = new StationGetAllResponseEntity();
            stationGetAllResponseEntity.setId(station.getId());
            stationGetAllResponseEntity.setNumber(station.getNumber());
            stationGetAllResponseEntity.setDescription(station.getDescription());
            stationGetAllResponseEntity.setLatitude(station.getLatitude());
            stationGetAllResponseEntity.setLongitude(station.getLongitude());
            stationGetAllResponseEntity.setAltitude(station.getAltitude());
            stationGetAllResponseEntity.setType(station.getType());

            List<VehicleEntityForStation> vehicleEntities= new ArrayList<>();

            for(Vehicle vehicle: station.getVehicles()){
                VehicleEntityForStation vehicleEntityForStation = new VehicleEntityForStation();
                vehicleEntityForStation.setNumber(vehicle.getNumber());
                vehicleEntityForStation.setStatus(vehicle.getStatus());
                vehicleEntities.add(vehicleEntityForStation);
            }

            stationGetAllResponseEntity.setVehicles(vehicleEntities);

            stationGetAllResponseEntities.add(stationGetAllResponseEntity);
        }

        return stationGetAllResponseEntities;
    }

    public String getStationNumber(Integer id) {
        Optional<Station> optionalStation = stationRepository.findById(id);
        if(optionalStation.isEmpty()) {
            throw new IllegalStateException("Station with id: " + id + " does not exist");
        }

        return optionalStation.get().getNumber();
    }

    public void changeStation(StationChangeRequest requestBody) {
        Optional<Station> optionalStation = stationRepository.findById(requestBody.getId());
        if(optionalStation.isEmpty()) {
            throw new IllegalStateException("Station with id: "
                    + requestBody.getId() + " does not exist");
        }

        Station station = optionalStation.get();
        station.setNumber(requestBody.getNumber());
        station.setDescription(requestBody.getDescription());
        station.setLatitude(requestBody.getLatitude());
        station.setLongitude(requestBody.getLongitude());
        station.setAltitude(requestBody.getAltitude());
        station.setType(requestBody.getType());
        stationRepository.saveAndFlush(station);
    }

    public void deleteStation(String number) {
        Optional<Station> optionalStation = stationRepository.findByNumber(number);
        if(optionalStation.isEmpty()) {
            throw new IllegalStateException("Station with number: " + number + " does not exist");
        }

        Station station = optionalStation.get();
        if(!station.getVehicles().isEmpty()) {
            throw new IllegalStateException("Station with number: " + number + " has vehicles");
        }

        stationRepository.delete(station);
    }
}
