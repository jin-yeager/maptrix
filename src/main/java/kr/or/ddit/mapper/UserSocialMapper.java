package kr.or.ddit.mapper;

import kr.or.ddit.vo.UserSocialVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSocialMapper {

    // VO.provider, VO.providerUserId 로 조회
    UserSocialVO selectByProviderAndId(UserSocialVO social);
}