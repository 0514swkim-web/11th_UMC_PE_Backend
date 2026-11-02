package com.umc.movie.repository;

import com.umc.movie.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // 최신순 (rating_id 내림차순)
    List<Rating> findByMember_MemberIdOrderByRatingIdDesc(Long memberId);

    // _ 는 연관 엔티티를 타고 들어가라는 뜻 → rating.member.memberId와 rating.movieId로 존재 여부 확인
    boolean existsByMember_MemberIdAndMovieId(Long memberId, Long movieId);
}
