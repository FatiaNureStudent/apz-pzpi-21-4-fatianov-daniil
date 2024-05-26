package com.nure.apz.fatianov.daniil.vehiclestationservice.station;

import com.nure.apz.fatianov.daniil.vehiclestationservice.station.request.StationAddRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.station.request.StationChangeRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.station.response.StationGetAllResponseEntity;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.request.VehicleChangeRequest;
import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.response.VehicleGetAllResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/vehicle-station/vehicle")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @PutMapping("/add")
    public ResponseEntity<String> addStation(
            @RequestBody StationAddRequest requestBody
            ) {
        try{
            stationService.addStation(requestBody) ;
            return ResponseEntity.ok().body("Station added successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<StationGetAllResponseEntity>> getAllStations() {
        try{
            return ResponseEntity.ok().body(stationService.getAllStations());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    @GetMapping("/get-number")
    public ResponseEntity<String> getStationNumber(
            @RequestParam Integer id
    ) {
        try{
            return ResponseEntity.ok().body(stationService.getStationNumber(id));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/get-number")
    public ResponseEntity<String> changeVehicle(
            @RequestBody StationChangeRequest requestBody
    ){
        try{
            stationService.changeStation(requestBody) ;
            return ResponseEntity.ok().body("Station changed successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteStation(
            @RequestParam String number
    ){
        try{
            stationService.deleteStation(number) ;
            return ResponseEntity.ok().body("Station deleted successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
