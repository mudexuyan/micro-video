package edu.ustc.controller;

import edu.ustc.constants.RedisPrefix;
import edu.ustc.dto.AdminDTO;
import edu.ustc.entity.Admin;
import edu.ustc.service.AdminService;
import edu.ustc.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * (Admin)表控制层
 *
 * @author makejava
 * @since 2022-09-30 16:32:00
 */
@RestController
public class AdminController {

    private AdminService adminService;
    private RedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public AdminController(AdminService adminService, RedisTemplate redisTemplate) {
        this.adminService = adminService;
        this.redisTemplate = redisTemplate;
    }

    //登录
    @PostMapping("/tokens")
    public Map<String, String> tokens(@RequestBody Admin admin, HttpSession session) {
        Map<String,String> res = new HashMap<>();
        log.info("admin对象：" + JSONUtils.writeJSON(admin));
        Admin adminDB = adminService.login(admin);
        //登录成功，生成token
        String token = session.getId();
        redisTemplate.opsForValue().set(RedisPrefix.TOKEN_KEY+token, adminDB, 90, TimeUnit.MINUTES);
        res.put("token",token);

        return res;
    }
    //用户信息
    @GetMapping("/admin-user")
    public AdminDTO getAdminUser(String token){
        Admin admin =  (Admin) redisTemplate.opsForValue().get(RedisPrefix.TOKEN_KEY+token);
        AdminDTO adminDTO = new AdminDTO();
        // 属性复制
        BeanUtils.copyProperties(admin,adminDTO);

        return adminDTO;
    }

    //退出登录
    @DeleteMapping("/tokens/{token}")
    public void logout(@PathVariable("token") String token){
        redisTemplate.delete(RedisPrefix.TOKEN_KEY+token);
    }

    @GetMapping("test")
    public String test(){
        Admin admin = adminService.queryById(1);
        return admin.getUsername();
    }


}

