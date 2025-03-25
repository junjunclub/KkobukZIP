package com.turtlecoin.auctionservice.domain.auction.controller;

import com.turtlecoin.auctionservice.domain.auction.dto.*;
import com.turtlecoin.auctionservice.domain.auction.service.AuctionService;
import com.turtlecoin.auctionservice.domain.auction.service.SseService;
import com.turtlecoin.auctionservice.global.ApiResponse;
import com.turtlecoin.auctionservice.global.utils.JWTUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionService auctionService;
    private final SseService sseService;
    private final JWTUtil jwtUtil;

    // 테스트
    @GetMapping("/test")
    public ResponseEntity<String> test () {
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    // SSE 연결
    @GetMapping(value = "/sse/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@PathVariable Long id) {
        try{
            log.info(id + "로 SSE요청이 들어왔음");

            HttpHeaders responseHeader = new HttpHeaders();
            responseHeader.add("Cache-Control", "no-cache");
            responseHeader.add("X-Accel-Buffering", "no");

            return new ResponseEntity<>(sseService.subscribe(id), responseHeader, HttpStatus.OK);
        }catch (Exception e) {
            return null;
        }
    }

    // SSE 보내기 테스트
    @PostMapping(value = "/sse/{id}")
    public void sendSSE(@PathVariable Long id, @RequestBody Map<String, String> request) {
        sseService.notify(id, request.get("message"));
    }

    // 경매 등록
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<Void> registerAuction(
            @RequestPart("data") @Valid CreateAuctionRequestDto createAuctionRequestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> multipartFiles) {
            auctionService.registerAuction(createAuctionRequestDto, multipartFiles);
            return ApiResponse.success(HttpStatus.OK, "경매 등록에 성공했습니다.");
    }

    // 경매 조회
    @GetMapping
    public ApiResponse<AuctionFilterResultDto> getAuctions(@ModelAttribute @Valid AuctionQueryParamsDto filter) {
        return ApiResponse.success(HttpStatus.OK, auctionService.getFilteredAuctions(filter), "경매 조회에 성공했습니다.");
    }


    @GetMapping("/{auctionId}")
    public ApiResponse<AuctionDetailResponseDto> getAuctionById(@PathVariable Long auctionId) {
        return ApiResponse.success(HttpStatus.OK, auctionService.getAuctionById(auctionId), "상세 경매 조회에 성공했습니다.");
    }


    @GetMapping("/my")
    public ApiResponse<AuctionListResponseDto> getMyAuctions(@RequestHeader("Authorization") String token) {
        Long id = jwtUtil.getUserIdFromToken(token);
        return ApiResponse.success(HttpStatus.OK, auctionService.getMyAuctions(id), "내 경매 조회에 성공했습니다.");
    }

}

