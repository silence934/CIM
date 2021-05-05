package xyz.nyist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: silence
 * @Date: 2021/5/2 23:34
 * @Description:
 */
@RestController
public class TestController {

    @GetMapping("/index")
    public String index(){
        return "index";
    }
}
