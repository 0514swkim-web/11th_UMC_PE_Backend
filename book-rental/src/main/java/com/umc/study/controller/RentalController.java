package com.umc.study.controller;

import com.umc.study.dto.CreateRentalRequest;
import com.umc.study.dto.RentalResponse;
import com.umc.study.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    // POST http://localhost:8080/rentals  { "userId": 1, "bookId": 3 } → 201 Created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse createRental(@Valid @RequestBody CreateRentalRequest request) {
        return rentalService.createRental(request);
    }

    // PATCH http://localhost:8080/rentals/1/return
    @PatchMapping("/{rentalId}/return")
    public RentalResponse returnRental(@PathVariable Long rentalId) {
        return rentalService.returnRental(rentalId);
    }
}
