package xyz.nyist.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.nyist.component.UserContext;
import xyz.nyist.entity.CrowdEntity;
import xyz.nyist.entity.CrowdUserEntity;
import xyz.nyist.result.Result;
import xyz.nyist.service.CrowdService;
import xyz.nyist.vo.CrowdDetailVO;
import xyz.nyist.vo.CrowdVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-03-15 20:57
 * @Description :
 */
@Slf4j
@RestController
@RequestMapping("/crowd")
public class CrowdController {
    @Autowired
    private UserContext userContext;
    @Autowired
    private CrowdService crowdService;

    @PostMapping("addCrowd")
    public Result<Void> create(String name) {
        CrowdEntity crowd = CrowdEntity.builder()
                .lordId(userContext.getCurrentUser().getId())
                .name(name)
                .build();

        crowd.setCrowdUsers(Collections.singletonList(CrowdUserEntity.builder()
                .crowd(crowd)
                .user(userContext.getCurrentUser())
                .build()));

        crowdService.create(crowd);
        return Result.success();
    }

    @PostMapping("jsonCrowd")
    public Result<Void> jsonCrowd(Integer userId, Integer crowdId) {
        crowdService.jsonCrowd(userId, crowdId);
        return Result.success();
    }

    @PostMapping("quitCrowd")
    public Result<Void> quitCrowd(Integer crowdId) {
        crowdService.quitCrowd(userContext.getCurrentUser().getId(), crowdId);
        return Result.success();
    }

    @GetMapping("getCrowdList")
    public Result<List<CrowdVO>> findAll() {
        return Result.success(userContext.getCurrentUser().getCrowds().stream()
                .map(CrowdVO::forValue).collect(Collectors.toList()));
    }

    @GetMapping("getCrowd")
    public Result<CrowdVO> findByName(String name) {
        return Result.success(CrowdVO.forValue(crowdService.findByName(name)));
    }

    @GetMapping("getCrowdInfo")
    public Result<CrowdDetailVO> getCrowdInfo(Integer id) {
        return Result.success(CrowdDetailVO.forValue(crowdService.getById(id)));
    }

}
