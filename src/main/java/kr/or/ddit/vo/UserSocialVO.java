package kr.or.ddit.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserSocialVO {
    private String email;
    private String provider;
    private String providerUserId;
    private String accessToken;
    private String refreshToken;
}