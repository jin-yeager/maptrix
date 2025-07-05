package kr.or.ddit.controller;

public class BCryptTest {
    public static void main(String[] args) {
        String raw = "java";  // 실제 입력하시는 비밀번호
        String hash = "$2b$12$7E8R4syt542AWfb1mRBjSOvM2NSrWDTKLfZ5lZQA…";  // DB에서 복사한 값
        boolean matches = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches(raw, hash);
        System.out.println("비밀번호 매칭 결과: " + matches);
    }
}
