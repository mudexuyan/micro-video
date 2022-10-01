package edu.ustc.controller;

import edu.ustc.entity.User;
import edu.ustc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户(User)表控制层
 *
 * @author makejava
 * @since 2022-10-01 12:50:30
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户列表
     */
    @GetMapping
    public Map<String, Object> users(@RequestParam(value = "page", defaultValue = "1") Integer pageNow,
                                     @RequestParam(value = "per_page", defaultValue = "5") Integer rows,
                                     @RequestParam(required = false) String id,
                                     String name,
                                     String phone
    ) {
        Map<String, Object> result = new HashMap<>();
        //查询用户 分页查询用户信息  指定条件分页查询用户信息
        List<User> items = userService.findAllByKeywords(pageNow, rows, id, name, phone);
        //查询总条数
        Long totalCounts = userService.findTotalCountsByKeywords(id, name, phone);
        result.put("total_count", totalCounts);
        result.put("items", items);

        return result;

    }
}

