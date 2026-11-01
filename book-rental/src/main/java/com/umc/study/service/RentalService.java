package com.umc.study.service;

import com.umc.study.dto.CreateRentalRequest;
import com.umc.study.dto.RentalResponse;
import com.umc.study.entity.Book;
import com.umc.study.entity.Rental;
import com.umc.study.entity.User;
import com.umc.study.exception.NotFoundException;
import com.umc.study.repository.BookRepository;
import com.umc.study.repository.RentalRepository;
import com.umc.study.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public RentalResponse createRental(CreateRentalRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 도서입니다."));

        Rental rental = Rental.start(user, book, LocalDateTime.now());
        return RentalResponse.from(rentalRepository.save(rental));
    }

    @Transactional
    public RentalResponse returnRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new NotFoundException("대여 기록을 찾을 수 없습니다."));
        rental.markReturned(LocalDateTime.now());
        return RentalResponse.from(rental);
    }
}
