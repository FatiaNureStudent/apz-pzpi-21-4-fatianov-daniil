package com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle;

import com.nure.apz.fatianov.daniil.vehiclestationservice.station.Station;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue
    private Integer id;
    private String number;
    private Double liftingCapacity;// in kilograms
    private Double flightDistance;// in kilometers
    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;
    private Status status;

}
