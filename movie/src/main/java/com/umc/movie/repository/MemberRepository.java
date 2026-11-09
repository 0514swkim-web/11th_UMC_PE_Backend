package com.umc.movie.repository;

import com.umc.movie.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    // 7주차: 로그인할 때 이메일로 회원 꺼내기 (없을 수도 있어서 Optional)
    Optional<Member> findByEmail(String email);
}
