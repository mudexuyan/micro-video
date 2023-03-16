package edu.ustc.controller;

import com.alibaba.fastjson.JSONObject;
import edu.ustc.entity.Video;
import edu.ustc.service.VideoService;
import edu.ustc.utils.JSONUtils;
import edu.ustc.utils.QiniuOssUtil;
import edu.ustc.vo.ModelVO;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 视频(Video)表控制层
 *
 * @author makejava
 * @since 2022-10-01 14:33:51
 */
@RestController
@RequestMapping("/videos")
public class VideoController {

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public Map<String, Object> videos(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "per_page", defaultValue = "5") Integer rows,
                                      String id,    //视频id
                                      String name,  //视频名称
                                      @RequestParam(value = "category_id", required = false) String categoryId,  //类别id
                                      @RequestParam(value = "uploader_name", required = false) String username   //根据up主 根据用户名查
    ) {
        Map<String, Object> result = new HashMap<>();
        log.info("当前页为: {}", page);
        log.info("每页显示记录数: {}", rows);
        log.info("搜索条件id是否存在:{}, id为: {}", !ObjectUtils.isEmpty(id), id);
        log.info("搜索条件name是否存在:{}, name为: {}", !ObjectUtils.isEmpty(name), name);
        log.info("搜索条件category_id是否存在:{}, category_id为: {}", !ObjectUtils.isEmpty(categoryId), categoryId);
        log.info("搜索条件uploader_name是否存在:{}, uploader_name为: {}", !ObjectUtils.isEmpty(username), username);
        //根据条件搜索的服务条件的记录
        Long totalCounts = videoService.findTotalCountsByKeywords(id, name, categoryId, username);
        //根据条件搜索的结果集合
        List<Video> items = videoService.findAllByKeywords(page, rows, id, name, categoryId, username);
        log.info("符合条件的总数: {}", totalCounts);
        result.put("total_count", totalCounts);
        result.put("items", items);
        return result;
    }

    @PatchMapping("/{id}")
    public Video update(@PathVariable("id") Integer id, @RequestBody Video video){
        log.info("更新类别");

        log.info(JSONUtils.writeJSON(video));
        video.setId(id);
        return videoService.update(video);
    }

    @PostMapping(value = "/model", produces = "application/json;charset=UTF-8")
    //MultipartFile file:用来接收上传视频信息
    //使用video对象接收 视频标题  视频简介  video{title,intro}
    //category_id 代表当前视频分类id
    //request:    当前请求上下文中存在用户信息
    public Map<String,Object> publishVideos(MultipartFile file, HttpServletRequest request) throws IOException {

        //1.获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        log.info("接收文件名称: {}", originalFilename);
//        log.info("接收到视频信息: " + new ObjectMapper().writeValueAsString(video));
//        log.info("类别id: {}", category_id);
        log.info("文件大小为: {}", file.getSize());

        //2.获取文件后缀 mp4 avi ....
        String ext = FilenameUtils.getExtension(originalFilename);

        //3.生成uuid
        String uuidFileName = UUID.randomUUID().toString().replace("-", "");

        //4.生成uuid文件名名称
        String newFileName = "测试视频" + uuidFileName + "." + ext;

        //5.上传阿里云oss 返回文件在oss地址
        String url = QiniuOssUtil.upload(file.getInputStream(), "videos", newFileName);
//        String url = "http://rri7sgufa.hd-bkt.clouddn.com/videos/%E6%B5%8B%E8%AF%95%E8%A7%86%E9%A2%91e065e8a6255e49d486645e36d4305232.mp4";

//        log.info("上传成功返回的地址: {}", url);

        //阿里云oss截取视频中某一帧作为封面
//        String cover = url + "?x-oss-process=video/snapshot,t_2000,f_jpg,w_0,h_0,m_fast,ar_auto";

        String cover = url + "?vframe/jpg/offset/1/w/480/h/360";

        log.info("七牛云oss根据url截取视频封面: {}", cover);

        // 请求头信息
        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "multipart/form-data;charset=gbk");
        headers.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
//        headers.add("headParam1", "headParamValue");
        MultiValueMap<String,String> map = new LinkedMultiValueMap();
        map.add("video",url);
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        String response = restTemplate.postForObject("http://172.16.227.68:1111/model", map, String.class);


        log.info("返回结果: {}",response);
        List<ModelVO> list =  JSONObject.parseArray(response,ModelVO.class);

        log.info("返回结果: {}",list);
        log.info("返回分类: {}",list.get(0).getCategory());


        //6.设置视频信息
        Video video = new Video();
        video.setCover(cover);//设置视频封面
        video.setLink(url);//设置视频地址
        video.setTitle("审核");
        video.setIntro("管理员审核视频内容");
        video.setCategoryId(list.get(0).getIndex());//设置类别id
//        video.setCategoryId(0);//设置类别id
        video.setUid(0);//设置发布用户id
        //调用视频服务
        Video videoResult = videoService.insert(video);
        log.info("管理员测试视频: {}", JSONUtils.writeValueAsString(videoResult));
        Map<String, Object> result = new HashMap<>();
        result.put("url",url);
        result.put("list",list);


        return result;
    }
}



