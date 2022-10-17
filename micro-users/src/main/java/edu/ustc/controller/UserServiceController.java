package edu.ustc.controller;

import edu.ustc.entity.User;
import edu.ustc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserServiceController {

    private static final Logger log = LoggerFactory.getLogger(UserServiceController.class);
    @Autowired
    private UserService userService;

    //根据用户id返回用户信息服务
    @GetMapping("/userInfo/{id}")
    public User user(@PathVariable("id") String id) {
        log.info("接收到用户id: {}", id);
        User user = userService.queryById(Integer.valueOf(id));
        return user;
    }

}
