package kr.or.ddit.mapper;

import org.apache.ibatis.annotations.Mapper;
import kr.or.ddit.vo.UsersVO;

@Mapper
public interface UsersMapper {
    // UserVO.user.getEmail()를 이용해 조회
    UsersVO selectByEmail(String email);

}