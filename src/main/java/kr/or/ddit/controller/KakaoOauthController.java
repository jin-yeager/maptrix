package kr.or.ddit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import kr.or.ddit.service.UserService;
import kr.or.ddit.vo.UserSocialVO;
import kr.or.ddit.vo.UsersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class KakaoOauthController {

    private static final String REST_API_KEY = "efb4ae9d464b6b29c8624d3793022e6e";
    private static final String REDIRECT_URI = "http://localhost/oauth/kakao/callback";

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 1) 카카오 인증 페이지로 리다이렉트
     */
    @GetMapping("/oauth/kakao")
    public String redirectToKakao() throws Exception {
        String authorizeUrl = "https://kauth.kakao.com/oauth/authorize"
            + "?response_type=code"
            + "&client_id=" + REST_API_KEY
            + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);
        return "redirect:" + authorizeUrl;
    }

    /**
     * 2) 카카오 콜백 핸들러
     */
    @GetMapping("/oauth/kakao/callback")
    public String kakaoCallback(
        @RequestParam("code") String code,
        HttpSession session
    ) throws Exception {
        // --- 2-1) 액세스 토큰 교환 ---
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String tokenRequestBody = "grant_type=authorization_code"
            + "&client_id=" + REST_API_KEY
            + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
            + "&code=" + code;
        HttpEntity<String> tokenRequest = new HttpEntity<>(tokenRequestBody, tokenHeaders);
        ResponseEntity<String> tokenResponse = restTemplate.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            tokenRequest,
            String.class
        );
        JsonNode tokenNode = objectMapper.readTree(tokenResponse.getBody());
        String accessToken = tokenNode.get("access_token").asText();

        // --- 2-2) 프로필 정보 조회 ---
        HttpHeaders profileHeaders = new HttpHeaders();
        profileHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> profileRequest = new HttpEntity<>(profileHeaders);
        ResponseEntity<String> profileResponse = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET,
            profileRequest,
            String.class
        );
        JsonNode profile = objectMapper.readTree(profileResponse.getBody());
        String kakaoId  = profile.get("id").asText();
        String nickname = profile.path("properties").path("nickname").asText();

        // --- 2-3) USER_SOCIAL 매핑 조회/등록 ---
        UserSocialVO social = userService.findSocial("KAKAO", kakaoId);
        if (social == null) {
            // 최초 1회: 매핑 INSERT + ROLE_USER 권한 부여
            String email = "kakao@test.com";  // 미리 USERS 테이블에 존재해야 할 계정
            social = new UserSocialVO();
            social.setEmail(email);
            social.setProvider("KAKAO");
            social.setProviderUserId(kakaoId);
            social.setAccessToken(accessToken);
            userService.insertSocial(social);
            userService.addAuth(email, "ROLE_USER");
        } else {
            // 이후 호출: 토큰만 갱신
            userService.updateSocialToken("KAKAO", kakaoId, accessToken);
        }

        // --- 2-4) 세션에 로그인 처리 ---
        UsersVO user = userService.findByEmail(social.getEmail());
        session.setAttribute("loginUser", user);


        // 3) 유저 조회
        UsersVO usersVO = userService.findByEmail(social.getEmail());

// 4) Spring Security 인증객체 생성
        // --- ② UserDetailsService 에서 UserDetails 로딩 ---
        UserDetails userDetails = userDetailsService.loadUserByUsername(social.getEmail());

        // --- ③ AuthenticationToken 생성 & SecurityContext에 저장 ---
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

    // 5) SecurityContext 에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        );
        return "redirect:/";
    }
}
