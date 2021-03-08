package xyz.nyist.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author : fucong
 * @Date: 2021-03-06 15:47
 * @Description :
 */
@RestController
public class TestController {


    @GetMapping("/test")
    @PreAuthorize("hasRole('permission1')")
    public String test(HttpServletRequest request) {
        return "index";
    }

}
