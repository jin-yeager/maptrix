package kr.or.ddit.service.impl;

import kr.or.ddit.vo.UsersVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.stream.Collectors;

// principal 역할의 CustomUser
public class CustomUser extends User {

    private UsersVO usersVO;

    // (Optional) 권한만 직접 넘겨줄 필요가 있을 때
    // 대장한테 넘겨주는거니까, 파라미터 순서만 맞으면된다
    public CustomUser(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
    }

    // 주로 이 생성자만 쓰시면 됩니다
    public CustomUser(UsersVO usersVO) {
        super(
            usersVO.getEmail(), 
            usersVO.getPassword(),
            usersVO.getUsersAuthVOList().stream()
                   .map(auth -> new SimpleGrantedAuthority(auth.getAuth()))
                   .collect(Collectors.toList())
        );
        this.usersVO = usersVO;
    }

    public UsersVO getUsersVO() {
        return usersVO;
    }

    public void setUsersVO(UsersVO usersVO) {
        this.usersVO = usersVO;
    }
}
