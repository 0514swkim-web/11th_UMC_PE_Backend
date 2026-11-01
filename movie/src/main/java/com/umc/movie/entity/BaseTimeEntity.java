package com.umc.movie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// 여러 엔티티가 공통으로 쓰는 생성·수정 시각 (테이블은 따로 없음)
@Getter
@MappedSuperclass
public abstract class BaseTimeEntity {

    // 처음 저장될 때 Hibernate가 채우고, 이후에는 바뀌지 않음
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 저장될 때마다 현재 시각으로 갱신
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
