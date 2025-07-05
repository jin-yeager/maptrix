package kr.or.ddit.service.impl;

import kr.or.ddit.mapper.UsersMapper;
import kr.or.ddit.vo.UsersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UsersMapper usersMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UsersVO usersVO = this.usersMapper.selectByEmail(email);


        return usersVO == null?null : new CustomUser(usersVO);
    }
}
