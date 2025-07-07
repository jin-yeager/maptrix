// src/main/java/kr/or/ddit/mapper/UserSocialMapper.java
package kr.or.ddit.mapper;

import kr.or.ddit.vo.UserSocialVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSocialMapper {
    UserSocialVO selectByProviderAndProviderUserId(String provider, String providerUserId);

    void insertSocial(UserSocialVO social);

    void updateAccessToken(String provider, String providerUserId, String accessToken);
}
