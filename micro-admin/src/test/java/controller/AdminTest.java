package controller;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class AdminTest {
    public static void main(String[] args) {
        String s = "123456";
        String password = DigestUtils.md5DigestAsHex(s.getBytes(StandardCharsets.UTF_8));
        System.out.println(password);

    }

}
