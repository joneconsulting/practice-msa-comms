package com.example.userservice.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // 사용자 또는 IP별로 bucket을 관리할 Map
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // 제한 정책: 예를 들어 1분에 10회 제한
    private final Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
    private final Bandwidth limit = Bandwidth.classic(10, refill);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();  // 클라이언트 IP 기준 제한
        Bucket bucket = cache.computeIfAbsent(ip, k -> Bucket.builder().addLimit(limit).build());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response); // 허용 범위 내 호출 처리
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429 상태코드 반환
            response.getWriter().write("Rate limit exceeded. Try again later.");
        }
    }
}