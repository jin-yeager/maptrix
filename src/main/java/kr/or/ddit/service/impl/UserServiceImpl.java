package kr.or.ddit.service.impl;

import kr.or.ddit.mapper.UsersMapper;
import kr.or.ddit.mapper.UserSocialMapper;
import kr.or.ddit.service.UserService;
import kr.or.ddit.vo.UsersVO;
import kr.or.ddit.vo.UserSocialVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UserSocialMapper userSocialMapper;

    // — 폼 로그인
    @Override
    public UsersVO findByEmail(String email) {
        return usersMapper.selectByEmail(email);
    }

    @Override
    @Transactional
    public void registerUser(UsersVO user) {
        usersMapper.insertUser(user);
        usersMapper.insertAuth(user.getEmail(), "ROLE_USER");
    }

    @Override
    public void addAuth(String email, String role) {
        usersMapper.insertAuth(email, role);
    }

    // — 소셜 로그인
    @Override
    public UserSocialVO findSocial(String provider, String providerUserId) {
        return userSocialMapper.selectByProviderAndProviderUserId(provider, providerUserId);
    }

    @Override
    @Transactional
    public void insertSocial(UserSocialVO social) {
        userSocialMapper.insertSocial(social);
    }

    @Override
    public void updateSocialToken(String provider, String providerUserId, String accessToken) {
        userSocialMapper.updateAccessToken(provider, providerUserId, accessToken);
    }
}
