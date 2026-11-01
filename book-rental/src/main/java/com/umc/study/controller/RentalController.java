package com.umc.study.controller;

import com.umc.study.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    // POST http://localhost:8080/rentals  { "userId": 1, "bookId": 3 }
    @PostMapping
    public String createRental(@RequestBody Map<String, Object> body) {
        rentalService.createRental(body);
        return "대여 기록이 생성되었습니다!";
    }

    // PATCH http://localhost:8080/rentals/1/return
    @PatchMapping("/{rentalId}/return")
    public ResponseEntity<String> returnRental(@PathVariable Long rentalId) {
        if (!rentalService.returnRental(rentalId)) {
            return ResponseEntity.status(404).body("대여 기록을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("반납 처리가 완료되었습니다!");
    }
}
