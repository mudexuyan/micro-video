package edu.ustc.feighclients;

import edu.ustc.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("ADMIN-USERS")
public interface UserClients {

    @GetMapping("/users/userInfo/{id}")
    User user(@PathVariable("id") String id);

//    @GetMapping("/userInfo/favorite")
//    Favorite favorite(@RequestParam("videoId") String videoId, @RequestParam("userId") String userId);
//
//    //用户评论
//    @PostMapping("/user/comment/{userId}/{videoId}")
//    void comments(@PathVariable("userId") Integer userId,@PathVariable("videoId") Integer videoId, @RequestBody Comment comment);
//
//
//    //评论列表
//    @GetMapping("/user/comments")
//    public Map<String,Object> comments(@RequestParam("videoId") Integer videoId,
//                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
//                                       @RequestParam(value = "per_page",defaultValue = "15") Integer rows);
}
