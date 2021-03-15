package xyz.nyist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nyist.component.UserContext;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.result.Result;
import xyz.nyist.service.UserService;
import xyz.nyist.vo.UserVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author : fucong
 * @Date: 2021-03-12 16:17
 * @Description :
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserContext userContext;
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Result<UserVO> info() {
        return Result.success(UserVO.forValue(userContext.getCurrentUser()));
    }

    @GetMapping("/select/details")
    public Result<UserVO> getDetails(Integer id) {
        return Result.success(UserVO.forValue(userService.getById(id)));
    }

    @PostMapping("/select/details/list")
    public Result<List<UserVO>> getDetailsList(@RequestBody List<Integer> ids) {
        return Result.success(userService.getByIds(ids).stream()
                .map(UserVO::forValue).collect(Collectors.toList()));
    }

    @GetMapping("/getAvatar")
    public Result<String> getAvatar(Integer id) {
        UserEntity userEntity = userService.getById(id);
        return Result.success(userEntity.getAvatar());
    }

    @GetMapping("/findUser")
    public Result<UserVO> findUser(String key) {
        return Result.success(UserVO.forValue(userService.findByUsernameOrNickname(key)));
    }

}
