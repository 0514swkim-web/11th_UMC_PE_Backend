package com.umc.movie.controller;

import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.config.SwaggerExamples;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateFavoriteRequest;
import com.umc.movie.dto.FavoriteResponse;
import com.umc.movie.dto.FavoriteStatusResponse;
import com.umc.movie.interceptor.AuthInterceptor;
import com.umc.movie.service.FavoriteService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 8주차: 즐겨찾기 API 4개. URL·바디 어디에도 memberId가 없고, 인터셉터가 토큰에서 꺼내 준 값을 씀
// WebConfig는 "전부 잠그고 일부만 열기"라서 /favorites는 따로 설정하지 않아도 토큰이 필요함
// 9주차: 4개 모두 토큰이 필요하니 자물쇠를 클래스에 한 번만 붙임
@RestController
@RequiredArgsConstructor
@Tag(name = "즐겨찾기", description = "로그인한 회원의 영화 즐겨찾기 API")
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH) // 이 컨트롤러의 모든 API가 토큰을 요구
public class FavoriteController {

    private final FavoriteService favoriteService;

    // POST http://localhost:8080/favorites  { "movieId": 550 } → 201
    @Operation(
            summary = "즐겨찾기 등록 (토큰 필요)",
            description = "토큰의 주인인 회원이 TMDB 영화 ID 하나를 즐겨찾기에 추가합니다."
    )
    // 우리 공통 응답 DTO 이름도 ApiResponse라서, Swagger 쪽 애노테이션은 풀네임으로 적음
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "즐겨찾기 등록 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "COMMON201",
                              "message": "즐겨찾기를 등록했습니다.",
                              "result": { "favoriteId": 1, "movieId": 550, "createdAt": "2026-09-13T21:00:00" }
                            }
                            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.AUTH401_2))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "이미 즐겨찾기한 영화",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.FAVORITE409_1)))
    })
    @PostMapping("/favorites")
    public ResponseEntity<ApiResponse<FavoriteResponse>> create(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @RequestBody @Valid CreateFavoriteRequest request
    ) {
        FavoriteResponse response = favoriteService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("즐겨찾기를 등록했습니다.", response));
    }

    // GET http://localhost:8080/favorites → 200 + 내 즐겨찾기 목록(최신순)
    @Operation(
            summary = "내 즐겨찾기 목록 조회 (토큰 필요)",
            description = "토큰의 주인인 회원의 즐겨찾기를 최신순으로 돌려줍니다. 하나도 없으면 빈 배열입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공 (COMMON200, 없으면 result: [])"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = SwaggerExamples.AUTH401_DESCRIPTION,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.AUTH401_2)))
    })
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getMyFavorites(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        List<FavoriteResponse> response = favoriteService.getMyFavorites(memberId);
        return ResponseEntity.ok(ApiResponse.ok("내 즐겨찾기 목록을 조회했습니다.", response));
    }

    // GET http://localhost:8080/favorites/550 → 200 + { "isFavorite": true/false }
    @Operation(
            summary = "즐겨찾기 여부 조회 (토큰 필요)",
            description = "이 영화를 내가 즐겨찾기했는지 알려줍니다. 안 했어도 오류가 아니라 200 + isFavorite: false입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공 (COMMON200, isFavorite true/false)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = SwaggerExamples.AUTH401_DESCRIPTION,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.AUTH401_2)))
    })
    @GetMapping("/favorites/{movieId}")
    public ResponseEntity<ApiResponse<FavoriteStatusResponse>> getStatus(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Parameter(description = "TMDB 영화 ID", example = "550") @PathVariable Long movieId
    ) {
        FavoriteStatusResponse response = favoriteService.getStatus(memberId, movieId);
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기 여부를 조회했습니다.", response));
    }

    // DELETE http://localhost:8080/favorites/550 → 200 + result: null (평점 삭제와 같은 방식)
    @Operation(
            summary = "즐겨찾기 해제 (토큰 필요)",
            description = "내 즐겨찾기에서 이 영화를 뺍니다. 성공하면 result는 null입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "해제 성공 (COMMON200, result: null)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = SwaggerExamples.AUTH401_DESCRIPTION,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.AUTH401_2))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "내 즐겨찾기에 없는 영화 (FAVORITE404_1)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = SwaggerExamples.FAVORITE404_1)))
    })
    @DeleteMapping("/favorites/{movieId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(hidden = true) @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Parameter(description = "TMDB 영화 ID", example = "550") @PathVariable Long movieId
    ) {
        favoriteService.delete(memberId, movieId);
        return ResponseEntity.ok(ApiResponse.ok("즐겨찾기를 해제했습니다.", null));
    }
}
