package kr.or.ddit.service;

import kr.or.ddit.vo.UsersVO;
import kr.or.ddit.vo.UserSocialVO;

public interface UserService {
    // — 폼 로그인용
    UsersVO findByEmail(String email);
    void registerUser(UsersVO user);
    void addAuth(String email, String role);

    // — 소셜 로그인용
    UserSocialVO findSocial(String provider, String providerUserId);
    void insertSocial(UserSocialVO social);
    void updateSocialToken(String provider, String providerUserId, String accessToken);
}