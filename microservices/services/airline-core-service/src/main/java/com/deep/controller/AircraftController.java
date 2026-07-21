package com.deep.controller;

import com.deep.payload.request.AircraftRequest;
import com.deep.payload.response.AircraftResponse;
import com.deep.payload.response.ApiResponse;
import com.deep.service.AircraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aircraft")
public class AircraftController {
    private final AircraftService aircraftService;

    @PostMapping
    public ResponseEntity<AircraftResponse> createAircraft(
            @Valid @RequestBody AircraftRequest request,
            @RequestHeader("X-User-Id") Long ownerId
         ) {

        AircraftResponse aircraftResponse = aircraftService.createAircraft(request, ownerId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aircraftResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(
            @PathVariable Long id
    ) {
        AircraftResponse aircraftResponse = aircraftService.getAircraftById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aircraftResponse);
    }

    @GetMapping
    public ResponseEntity<List<AircraftResponse>> listAllAircraftByOwner(
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        List<AircraftResponse> aircraftResponses = aircraftService.listAllAircraftByOwner(ownerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aircraftResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponse> updateAircraft(
            @PathVariable Long id,
            @RequestBody AircraftRequest request,
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        AircraftResponse aircraftResponse = aircraftService.updateAircraft(id, request, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(aircraftResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAircraft(
            @PathVariable Long id
    ) {
        aircraftService.deleteAircraft(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse("Aircraft " + id + " deleted successfully!"));
    }
}
