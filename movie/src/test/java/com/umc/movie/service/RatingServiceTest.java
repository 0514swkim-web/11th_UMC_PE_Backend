package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// 9주차 필수 미션: 평점 서비스 단위 테스트 2개 (DB 없이 가짜 Repository로 Service 판단만 확인)
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository; // 진짜 DB 대신 들어가는 가짜

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService; // 위의 가짜 두 개를 주입받은 진짜 Service

    // 테스트에서 "나"(1번 회원)와 "다른 사람"(2번 회원)을 만드는 도우미
    // memberId는 원래 DB가 저장할 때 붙여 주는 값이라, DB가 없는 단위 테스트에서는 직접 넣어 줌
    private Member memberWithId(Long memberId, String email, String nickname) {
        Member member = Member.builder()
                .email(email)
                .password("암호화된비밀번호")
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(member, "memberId", memberId);
        return member;
    }

    @Test
    @DisplayName("같은 영화에 평점을 중복 등록하면 409 RATING409_1 예외를 던지고 저장하지 않는다")
    void createRatingWhenDuplicated() {
        // given: 1번 회원이 238번 영화에 이미 평점을 남긴 상황
        Member me = memberWithId(1L, "challenger@umc.com", "영화조아");
        given(memberRepository.findById(1L)).willReturn(Optional.of(me));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 238L)).willReturn(true);

        // when & then: 같은 영화로 다시 등록하면 RATING409_1 예외
        assertThatThrownBy(() -> ratingService.create(1L, new CreateRatingRequest(238L, 5, "또 남기기")))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT)
                .hasFieldOrPropertyWithValue("code", "RATING409_1");

        // 저장은 시도조차 하지 않음
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("남의 평점을 수정하려 하면 403 RATING403_1 예외를 던지고 평점은 그대로 둔다")
    void updateOthersRating() {
        // given: 10번 평점의 주인은 2번 회원(다른 사람), 별점 5점
        Member other = memberWithId(2L, "minseo@umc.com", "민서");
        Rating othersRating = Rating.builder()
                .member(other)
                .movieId(550L)
                .score(5)
                .comment("최고의 영화!")
                .build();
        given(ratingRepository.findById(10L)).willReturn(Optional.of(othersRating));

        // when & then: 1번 회원(나)이 그 평점을 1점으로 바꾸려 하면 RATING403_1 예외
        assertThatThrownBy(() -> ratingService.update(1L, 10L, new UpdateRatingRequest(1, "바꿔치기")))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                .hasFieldOrPropertyWithValue("code", "RATING403_1");

        // 예외에서 멈추지 않고 "값이 안 바뀌었다"까지 확인
        assertThat(othersRating.getScore()).isEqualTo(5);
        assertThat(othersRating.getComment()).isEqualTo("최고의 영화!");
        verify(ratingRepository, never()).save(any(Rating.class));
    }
}
