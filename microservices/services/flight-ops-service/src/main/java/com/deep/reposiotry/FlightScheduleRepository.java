package com.deep.reposiotry;

import com.deep.model.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {

    List<FlightSchedule> findByFlightId(Long flightId);

    // Spring Data traverses the nested Flight.airlineId property automatically
    List<FlightSchedule> findByFlightAirlineId(Long airlineId);

    @Query("SELECT fs FROM FlightSchedule fs WHERE fs.flight.id = :flightId AND fs.isActive = true " +
            "AND fs.startDate <= :date AND fs.endDate >= :date")
    List<FlightSchedule> findActiveSchedulesForDate(@Param("flightId") Long flightId, @Param("date") LocalDate date);
}