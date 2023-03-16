package edu.ustc.controller;

import com.alibaba.fastjson.JSONObject;
import edu.ustc.entity.Category;
import edu.ustc.service.CategoryService;
import edu.ustc.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

/**
 * 分类(Category)表控制层
 *
 * @author makejava
 * @since 2022-10-01 08:43:26
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //列表
    @GetMapping
    public List<Category> categoryList(){
       return categoryService.queryByFirstLevel();

    }

    //修改类别
    @PatchMapping("/{id}")
    public Category update(@PathVariable("id") Integer id,@RequestBody Category category){
        log.info("更新类别");

        log.info(JSONUtils.writeJSON(category));
        category.setId(id);
        return categoryService.update(category);
    }

    //添加
    @PostMapping
    public Category save(@RequestBody Category category){
        log.info(JSONUtils.writeJSON(category));
        return categoryService.insert(category);
    }

    //删除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id){
        Category category = categoryService.queryById(id);
        category.setDeletedAt(new Date());
        categoryService.update(category);
    }

    @GetMapping("/{id}")
    public Category category(@PathVariable("id") Integer id) {
        log.info("接收到的类别id: {}", id);
        Category category = categoryService.queryById(id);
        return category;
    }

    //调用算法的分类并存储到数据库中
    @GetMapping("/category")
    public void model() {
        log.info("调用算法的分类并存储到数据库中");

        // 请求头信息
        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "multipart/form-data;charset=gbk");
        headers.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
//        headers.add("headParam1", "headParamValue");
//        MultiValueMap<String,String> map = new LinkedMultiValueMap();
//        map.add("video",url);
        String response = restTemplate.getForObject("http://172.16.227.68:1111/category",  String.class);
        List<String> result =  JSONObject.parseArray(response,String.class);
        System.out.println(result);

        for(int i=0;i<result.size();++i){
            Category tmp = new Category();
            tmp.setName(result.get(i));
            categoryService.insert(tmp);
        }

    }

}

