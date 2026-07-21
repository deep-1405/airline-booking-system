package com.deep.controller;

import com.deep.enums.AirlineStatus;
import com.deep.payload.request.AirlineRequest;
import com.deep.payload.response.AirlineDropdownItem;
import com.deep.payload.response.AirlineResponse;
import com.deep.payload.response.ApiResponse;
import com.deep.service.AirlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/airlines")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @PostMapping
    public ResponseEntity<AirlineResponse> createAirline(
            @Valid @RequestBody AirlineRequest airlineRequest,
            // Reads the HTTP request header named X-User-Id and put its value into the userId variable.
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        airlineService.createAirline(airlineRequest, userId)
                );
    }

    @GetMapping("/admin")
    public ResponseEntity<AirlineResponse> getAirlineByOwner(
            // @Valid @RequestBody Long ownerId this is not good as it is riskier,
            // anyone can pass the malicious owner id and get the information,
            // so it is better to post it through the authenticated owner id through the header
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airlineService.getAirlineByOwner(ownerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineResponse> getAirlineById(
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airlineService.getAirlineById(id));
    }

    @GetMapping
    public ResponseEntity<Page<AirlineResponse>> getAllAirlines(
            @RequestParam(defaultValue = "asc") String sortDirection,
            // can not pass the Long or Integer here because the PageRequest.of expects a math number not an object
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airlineService.getAllAirlines(pageable));
    }

    @PutMapping
    public ResponseEntity<AirlineResponse> updateAirline(
            @Valid @RequestBody AirlineRequest request,
            @RequestHeader("X-User-Id") Long ownerId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airlineService.updateAirline(request, ownerId));
    }

    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteAirline(
//            @PathVariable Long id,
//            @RequestHeader("X-User-Id") Long ownerId
//    ) {
//        airlineService.deleteAirline(id, ownerId);
//        return ResponseEntity
//                .status(HttpStatus.NO_CONTENT)
//                .build();
//    }
    public ResponseEntity<ApiResponse> deleteAirline(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId)
    {
        airlineService.deleteAirline(id, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse("Airline " + id + " deleted successfully!"));
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<AirlineDropdownItem>> getAirlinesForDropdown() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(airlineService.getAirlinesForDropdown());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<AirlineResponse> approveAirline(
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(airlineService.changeStatusByAdmin(id, AirlineStatus.ACTIVE));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<AirlineResponse> suspendAirline(
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(airlineService.changeStatusByAdmin(id, AirlineStatus.INACTIVE));
    }

    @PostMapping("/{id}/ban")
    public ResponseEntity<AirlineResponse> banAirline(
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(airlineService.changeStatusByAdmin(id, AirlineStatus.BANNED));
    }
}
