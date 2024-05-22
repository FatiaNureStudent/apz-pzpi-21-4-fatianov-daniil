package com.nure.apz.fatianov.daniil.vehiclestationservice.station;

import com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "station")
public class Station {
    @Id
    @GeneratedValue
    private Integer id;
    private String number;
    private String description;
    private Type type;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    @OneToMany(mappedBy = "station")
    private List<Vehicle> vehicles;
}
