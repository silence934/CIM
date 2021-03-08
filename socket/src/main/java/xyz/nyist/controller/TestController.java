package xyz.nyist.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author : fucong
 * @Date: 2021-01-30 20:42
 * @Description :
 */
@Controller
public class TestController {

    @Value("${server.port}")
    private Integer port;
    @Value("${server.address}")
    private String address;

    @GetMapping("/index")
    public String index1(HttpServletRequest request) {
        request.setAttribute("address", "http://" + address + ":" + (port + 1) + "?mac=" + (port + 1));
        return "index";
    }


}
