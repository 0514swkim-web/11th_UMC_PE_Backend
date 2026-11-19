package com.umc.movie.dto;

import com.umc.movie.entity.Member;

import java.time.LocalDateTime;

// GET /members/me 응답. 엔티티를 그대로 내보내면 password까지 나가니까 DTO로 옮겨 담음
// 8주차: 프론트가 GET /members/{memberId}/ratings로 "내 평점"을 찾으려면 내 번호가 필요해서 memberId 추가
public record MemberInfoResponse(
        Long memberId,
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getCreatedAt()
        );
    }
}
