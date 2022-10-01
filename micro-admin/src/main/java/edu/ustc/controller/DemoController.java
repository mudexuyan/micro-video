package edu.ustc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demos")
public class DemoController {
    static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    @GetMapping
    public String demos(){
        logger.info("admin");
        return "admin";
    }
}
