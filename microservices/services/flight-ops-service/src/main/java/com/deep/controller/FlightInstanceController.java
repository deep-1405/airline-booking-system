package com.deep.controller;

import com.deep.payload.request.FlightInstanceRequest;
import com.deep.payload.response.FlightInstanceResponse;
import com.deep.service.FlightInstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flight-instances")
@RequiredArgsConstructor
public class FlightInstanceController {

    private final FlightInstanceService flightInstanceService;

    @PostMapping
    public ResponseEntity<FlightInstanceResponse> createFlightInstance(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody FlightInstanceRequest flightInstanceRequest
    ) throws Exception {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(flightInstanceService.createFlightInstanceWithCabins(userId, flightInstanceRequest));
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<Long, FlightInstanceResponse>> getFlightInstanceByIds(
            @RequestBody List<Long> ids
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(flightInstanceService.getFlightInstancesByIds(ids));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<FlightInstanceResponse> getFlightInstanceById(
            @PathVariable Long id
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(flightInstanceService.getFlightInstanceById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<FlightInstanceResponse>> getFlightInstanceById() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(flightInstanceService.getFlightInstances());
    }

    @GetMapping()
    public ResponseEntity<Page<FlightInstanceResponse>> getByAirlineId(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long departureAirportId,
            @RequestParam(required = false) Long arrivalAirportId,
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) LocalDate onDate,
            @PageableDefault(size = 20, sort = "departureDateTime", direction = Sort.Direction.ASC)Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    flightInstanceService.getByAirlineId(
                    userId,
                    departureAirportId,
                    arrivalAirportId,
                    flightId,
                    onDate,
                    pageable
                )
        );
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<FlightInstanceResponse> updateFlightInstance(
            @PathVariable Long id,
            @Valid @RequestBody FlightInstanceRequest flightInstanceRequest
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        flightInstanceService.updateFlightInstance(id, flightInstanceRequest)
                );
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteFlightInstance(@PathVariable Long id) {
        flightInstanceService.deleteFlightInstance(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
