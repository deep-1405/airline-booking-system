package com.deep.controller;

import com.deep.payload.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public ApiResponse homeController() {
        return new ApiResponse("Hello everyone, I am airline core service and I will Manage airlines, aircraft fleet, aircraft models, and operational inventory for the airline system.");
    }
}
