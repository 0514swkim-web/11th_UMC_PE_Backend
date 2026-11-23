package com.umc.movie.controller;

import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.config.SwaggerExamples;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateMovieRecordRequest;
import com.umc.movie.dto.MovieRecordResponse;
import com.umc.movie.interceptor.AuthInterceptor;
import com.umc.movie.service.MovieRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// 8주차: 평점 + 즐겨찾기를 한 트랜잭션으로 저장하는 학습용 API
@RestController
@RequiredArgsConstructor
@Tag(name = "영화 기록", description = "평점과 즐겨찾기를 한 번에 저장하는 API")
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public class MovieRecordController {

    private final MovieRecordService movieRecordService;

    // POST http://localhost:8080/movie-records
    // { "movieId": 238, "score": 5, "comment": "...", "addToFavorites": true } → 201
    @Operation(
            summary = "영화 기록 저장 (토큰 필요)",
            description = """
                    평점을 남기고, addToFavorites가 true면 즐겨찾기도 함께 추가합니다.
                    둘은 하나의 트랜잭션이라 즐겨찾기에서 실패하면 평점도 저장되지 않습니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "저장 성공 (COMMON201, favorite은 추가 안 했으면 null)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "입력값 오류 (COMMON4001)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.COMMON4001))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = SwaggerExamples.AUTH401_DESCRIPTION,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.AUTH401_2))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "이미 평점을 남김 (RATING409_1) / 이미 즐겨찾기함 (FAVORITE409_1)",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "RATING409_1", value = SwaggerExamples.RATING409_1),
                            @ExampleObject(name = "FAVORITE409_1", value = SwaggerExamples.FAVORITE409_1)
                    }))
    })
    @PostMapping("/movie-records")
    public ResponseEntity<ApiResponse<MovieRecordResponse>> create(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateMovieRecordRequest request
    ) {
        MovieRecordResponse response = movieRecordService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("영화 기록을 저장했습니다.", response));
    }
}
