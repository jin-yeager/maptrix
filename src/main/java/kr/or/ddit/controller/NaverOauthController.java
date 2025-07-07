package kr.or.ddit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import kr.or.ddit.service.UserService;
import kr.or.ddit.vo.UserSocialVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
public class NaverOauthController {

    private static final String NAVER_CLIENT_ID     = "osgXD67hSi8oHmLUm6fq";
    private static final String NAVER_CLIENT_SECRET = "NOzw85_kbB";
    private static final String REDIRECT_URI        = "http://localhost/oauth/naver/callback";
    private static final String SOCIAL_EMAIL        = "kakao@test.com"; // 하드코딩된 공통 이메일
    private static final String STATE               = UUID.randomUUID().toString();

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 1) 네이버 인증 페이지로 리다이렉트
     */
    @GetMapping("/oauth/naver")
    public String redirectToNaver() throws Exception {
        String authorizeUrl = "https://nid.naver.com/oauth2.0/authorize"
            + "?response_type=code"
            + "&client_id=" + NAVER_CLIENT_ID
            + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
            + "&state=" + STATE;
        return "redirect:" + authorizeUrl;
    }

    /**
     * 2) 네이버 콜백 핸들러
     */
    @GetMapping("/oauth/naver/callback")
    public String naverCallback(
        @RequestParam("code") String code,
        @RequestParam("state") String state,
        HttpSession session
    ) throws Exception {
        // state 검증
        if (!STATE.equals(state)) {
            throw new IllegalArgumentException("Invalid state parameter");
        }

        // --- 2-1) 액세스 토큰 교환 ---
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String tokenRequestBody = "grant_type=authorization_code"
            + "&client_id=" + NAVER_CLIENT_ID
            + "&client_secret=" + NAVER_CLIENT_SECRET
            + "&code=" + code
            + "&state=" + state;
        HttpEntity<String> tokenRequest = new HttpEntity<>(tokenRequestBody, tokenHeaders);
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(
            "https://nid.naver.com/oauth2.0/token",
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
            "https://openapi.naver.com/v1/nid/me",
            org.springframework.http.HttpMethod.GET,
            profileRequest,
            String.class
        );
        JsonNode profileNode = objectMapper.readTree(profileResponse.getBody())
            .path("response");
        String naverId  = profileNode.get("id").asText();

        // --- 2-3) USER_SOCIAL 매핑 조회/등록 ---
        UserSocialVO social = userService.findSocial("NAVER", naverId);
        if (social == null) {
            // 최초 1회: 하드코딩된 이메일 사용, 매핑 INSERT + 권한 부여
            social = new UserSocialVO();
            social.setEmail(SOCIAL_EMAIL);
            social.setProvider("NAVER");
            social.setProviderUserId(naverId);
            social.setAccessToken(accessToken);
            userService.insertSocial(social);
            userService.addAuth(SOCIAL_EMAIL, "ROLE_USER");
        } else {
            // 이후 호출: 토큰만 갱신
            userService.updateSocialToken("NAVER", naverId, accessToken);
        }

        // --- 2-4) Spring Security 인증 및 세션 저장 ---
        UserDetails userDetails = userDetailsService.loadUserByUsername(SOCIAL_EMAIL);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        );

        return "redirect:/";
    }
}
