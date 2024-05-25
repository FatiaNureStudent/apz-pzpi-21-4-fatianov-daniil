package com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle;

import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.request.VehicleAddRequest;
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
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/add")
    public ResponseEntity<String> addVehicle(
            @RequestBody VehicleAddRequest requestBody
    ) {
        try{
            vehicleService.addVehicle(requestBody) ;
            return ResponseEntity.ok().body("Vehicle added successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<VehicleGetAllResponseEntity>> getAllVehicles() {
        try{
            return ResponseEntity.ok().body(vehicleService.getAllVehicles());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    @GetMapping("/get-number")
    public ResponseEntity<String> getVehicleNumber(
            @RequestParam Integer id
    ) {
        try{
            return ResponseEntity.ok().body(vehicleService.getVehicleNumber(id));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change")
    public ResponseEntity<String> changeVehicle(
            @RequestBody VehicleChangeRequest requestBody
    ){
        try{
            vehicleService.changeVehicle(requestBody) ;
            return ResponseEntity.ok().body("Vehicle changed successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/get-vehicle-ready")
    public ResponseEntity<String> getVehicleReady(
            @RequestParam String number
    ){
        try{
            vehicleService.getVehicleReady(number) ;
            return ResponseEntity.ok().body("Vehicle added successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteVehicle(
            @RequestParam String number
    )
    {
        try{
            vehicleService.deleteVehicle(number) ;
            return ResponseEntity.ok().body("Vehicle deleted successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //TODO зробити SEND функцію

}
