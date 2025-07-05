package kr.or.ddit.config;

import jakarta.servlet.DispatcherType;
import kr.or.ddit.service.impl.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final UserDetailServiceImpl userDetailServiceImpl;

    public SecurityConfig(UserDetailServiceImpl userDetailServiceImpl) {
        this.userDetailServiceImpl = userDetailServiceImpl;
    }


//    HTTP 보안 필터 체인 설정
//    -CSRF 비활성화
//    -로그인 페이지와 리소스는 모두 접근 허용
//    -그 외 요청은 인증 필요
//    -폼 로그인 활성화
//    -JSP 뷰는 정적 리소스가 아니라
//    -컨트롤러가 처리하는 동적 리소스이기 때문에 컨트롤러 주소를 사용.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authz-> authz
                    // 포워딩 무조건 먼저 열어주고 시작해야 함.
                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ASYNC).permitAll()
                    .requestMatchers("/login","/","/css/**","/js/**","/authenticate").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/login") //커스텀 로그인 페이지
                    .loginProcessingUrl("/authenticate")//jsp용 POST 주소
                    .usernameParameter("email") //email로 <input name="email"> 매칭,
                                                //default: username이지만 난 email로 하겠어.
                    .defaultSuccessUrl("/",true) // 로그인 성공시 이동경로
                    .failureUrl("/login?error")
                    .permitAll()
                )
                // userDetailsService와 passwordEncoder 연결
                .userDetailsService(userDetailServiceImpl);

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/static/**");
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
