package com.api.v2.customers.controller;

import com.api.v2.customers.dtos.CustomerModificationDto;
import com.api.v2.customers.dtos.CustomerRegistrationDto;
import com.api.v2.customers.dtos.CustomerResponseDto;
import com.api.v2.customers.services.CustomerModificationService;
import com.api.v2.customers.services.CustomerRegistrationService;
import com.api.v2.customers.services.CustomerRetrievalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v2/customers")
public class CustomerController {

    private final CustomerRegistrationService registrationService;
    private final CustomerModificationService modificationService;
    private final CustomerRetrievalService retrievalService;

    public CustomerController(CustomerRegistrationService registrationService,
                              CustomerModificationService modificationService,
                              CustomerRetrievalService retrievalService
    ) {
        this.registrationService = registrationService;
        this.modificationService = modificationService;
        this.retrievalService = retrievalService;
    }

    @PostMapping
    public CustomerResponseDto register(@Valid @RequestBody CustomerRegistrationDto registrationDto) {
        return registrationService.register(registrationDto);
    }

    @PatchMapping("{id}")
    public void modify(@PathVariable String id, @Valid @RequestBody CustomerModificationDto modificationDto) {
        modificationService.modify(id, modificationDto);
    }

    @GetMapping("{id}")
    public CustomerResponseDto findById(@PathVariable String id) {
        return retrievalService.findById(id);
    }

    @GetMapping
    public List<CustomerResponseDto> findAll() {
        return retrievalService.findAll();
    }
}
