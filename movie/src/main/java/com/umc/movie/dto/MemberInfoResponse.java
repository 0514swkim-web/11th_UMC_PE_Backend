package com.umc.movie.dto;

import com.umc.movie.entity.Member;

import java.time.LocalDateTime;

// GET /members/me 응답. 엔티티를 그대로 내보내면 password까지 나가니까 DTO로 옮겨 담음
public record MemberInfoResponse(
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getEmail(),
                member.getNickname(),
                member.getCreatedAt()
        );
    }
}
