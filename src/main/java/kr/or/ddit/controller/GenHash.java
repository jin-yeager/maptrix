package kr.or.ddit.controller;

public class GenHash {
    public static void main(String[] args) {
        String raw = "java";
        String hash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(raw);
        System.out.println(hash);
    }
}