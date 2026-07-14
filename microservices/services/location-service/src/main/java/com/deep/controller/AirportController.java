package com.deep.controller;

import com.deep.payload.request.AirportRequest;
import com.deep.payload.response.AirportResponse;
import com.deep.payload.response.ApiResponse;
import com.deep.service.AirportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;

    // ResponseEntity<AirportResponse> let us control
    // Status Code
    // Headers
    // Body

    // Instead if we have said something like, it only means the body
    @PostMapping
    public ResponseEntity<AirportResponse> createAirport(
            // only when we apply @Valid annotation, then only the validation of the AirportRequest will apply
            @RequestBody @Valid AirportRequest request
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(airportService.createAirport(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportResponse> getAirportById(
            @PathVariable Long id
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airportService.getAirportById(id));
    }

    @GetMapping
    public ResponseEntity<List<AirportResponse>> getAllAirports() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airportService.getAllAirports());
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<AirportResponse>> getAirpotByCityId(
            @PathVariable Long cityId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airportService.getAirportByCityId(cityId));
    }

    @PutMapping("{id}")
    public ResponseEntity<AirportResponse> updateAirport(
            @PathVariable Long id,
            @Valid @RequestBody AirportRequest request
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airportService.updateAirport(id, request));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteAirport(
            @PathVariable Long id
    ) throws Exception {
        airportService.deleteAirport(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Airport with " + id + " deleted"));
    }
}
