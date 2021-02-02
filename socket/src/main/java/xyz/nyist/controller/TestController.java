package xyz.nyist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nyist.event.MessageEvent;

/**
 * @Author : fucong
 * @Date: 2021-01-30 20:42
 * @Description :
 */
@RestController
public class TestController {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusProperties busProperties;


    @GetMapping("/test")
    public void test() {
        applicationContext.publishEvent(new MessageEvent(this, busProperties.getId(), null));
    }

}
