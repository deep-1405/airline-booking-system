package com.deep.controller;

import com.deep.payload.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping
    public ApiResponse HomeController() {
        ApiResponse response = new ApiResponse();
        response.setMessage("Flight Operations Service manages Flights, Flight Schedules, and Flight Instances. It represents the core operational flight lifecycle");
        return  response;
    }
}
