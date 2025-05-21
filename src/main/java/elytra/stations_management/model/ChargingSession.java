package elytra.stations_management.model;

import java.time.LocalDateTime;

import elytra.stations_management.models.Station;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "charging_sessions")
public class ChargingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private double energyUsed;

    @Column(nullable = false)
    private double cost;

    public ChargingSession() {
    }

    public ChargingSession(Station station, LocalDateTime startTime, LocalDateTime endTime,
            double energyUsed, double cost) {
        this.station = station;
        this.startTime = startTime;
        this.endTime = endTime;
        this.energyUsed = energyUsed;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public double getEnergyUsed() {
        return energyUsed;
    }

    public double getCost() {
        return cost;
    }
}