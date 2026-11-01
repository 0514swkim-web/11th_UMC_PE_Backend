package com.umc.study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental {

    private static final int RENTAL_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "rented_at", nullable = false)
    private LocalDateTime rentedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    // 3주차 SQL의 NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY)를 자바 코드로 표현
    public static Rental start(User user, Book book, LocalDateTime now) {
        Rental rental = new Rental();
        rental.user = user;
        rental.book = book;
        rental.rentedAt = now;
        rental.dueAt = now.plusDays(RENTAL_DAYS);
        return rental;
    }

    // 트랜잭션 안에서 필드만 바꾸면 커밋할 때 JPA가 UPDATE를 실행 (변경 감지)
    public void markReturned(LocalDateTime now) {
        this.returnedAt = now;
    }
}
