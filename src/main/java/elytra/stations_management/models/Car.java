package elytra.stations_management.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "cars")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private Double batteryCapacity; 

    @Column(nullable = false)
    private String chargerType; 

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver evDriver;
}