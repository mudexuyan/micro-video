package edu.ustc.controller;

import edu.ustc.entity.Category;
import edu.ustc.service.CategoryService;
import edu.ustc.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}

