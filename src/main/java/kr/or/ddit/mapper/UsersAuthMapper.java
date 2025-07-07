package kr.or.ddit.mapper;

import kr.or.ddit.vo.UsersAuthVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UsersAuthMapper {
    // VO.email로 권한 리스트 조회
    List<UsersAuthVO> selectAuthsByEmail(UsersAuthVO userAuth);




}