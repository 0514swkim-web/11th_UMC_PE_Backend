package com.umc.movie.repository;

import com.umc.movie.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 내 즐겨찾기 목록, 최근에 등록한 것부터
    List<Favorite> findByMember_MemberIdOrderByFavoriteIdDesc(Long memberId);

    // 해제할 즐겨찾기 한 개 찾기
    Optional<Favorite> findByMember_MemberIdAndMovieId(Long memberId, Long movieId);

    // 등록 전 중복 검사, 여부 조회
    boolean existsByMember_MemberIdAndMovieId(Long memberId, Long movieId);
}
