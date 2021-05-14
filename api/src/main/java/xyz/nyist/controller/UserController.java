package xyz.nyist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.nyist.component.UserContext;
import xyz.nyist.dto.RetrievePasswordDTO;
import xyz.nyist.dto.UserRegisterDTO;
import xyz.nyist.dto.UserUpdateDTO;
import xyz.nyist.dto.VerificationCodeDTO;
import xyz.nyist.entity.CrowdEntity;
import xyz.nyist.entity.FriendEntity;
import xyz.nyist.entity.UserEntity;
import xyz.nyist.result.Result;
import xyz.nyist.service.*;
import xyz.nyist.vo.*;

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
    @Autowired
    private ApiUserService apiUserService;
    @Autowired
    private CrowdService crowdService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private RetrievePasswordService retrievePasswordService;

    @GetMapping("/info")
    public Result<UserVO> info() {
        return Result.success(UserVO.forValue(userContext.getCurrentUser()));
    }

    @GetMapping("/select/details")
    public Result<ChatSubjectVo> getDetails(Integer id) {
        if (id > 9999) {
            return Result.success(UserChatSubjectVo.forValue(crowdService.getById(id)));
        } else {
            return Result.success(UserChatSubjectVo.forValue(userService.getById(id)));
        }
    }

    @PostMapping("/select/chatsInfo")
    public Result<List<ChatSubjectVo>> getDetailsList(@RequestBody List<Integer> ids) {

        UserEntity currentUser = userContext.getCurrentUser();

        List<ChatSubjectVo> list = userService.getByIds(ids).stream()
                .map(user -> {
                    FriendEntity friend = friendService.getFriend(currentUser, user);
                    return UserChatSubjectVo.forValue(user, friend);
                }).collect(Collectors.toList());


        list.addAll(crowdService.getByIds(ids).stream()
                .map(CrowChatSubjectVo::forValue).collect(Collectors.toList()));
        return Result.success(list);
    }

    @GetMapping("/getAvatar")
    public Result<String> getAvatar(Integer id) {
        if (id < 10000) {
            UserEntity userEntity = userService.getById(id);
            return Result.success(userEntity.getAvatar());
        } else {
            CrowdEntity crowd = crowdService.getById(id);
            return Result.success(crowd.getAvatar());
        }
    }

    @GetMapping("/findUser")
    public Result<FindFriendUserVO> findUser(String key) {
        UserEntity user = userService.findByUsernameOrNickname(key);
        FindFriendUserVO findFriendUser = FindFriendUserVO.forValue(user,
                userContext.getCurrentUser().getGroups().stream()
                        .flatMap(g -> g.getFriends().stream())
                        .anyMatch(f -> f.getUserOne().equals(user) || f.getUserTwo().equals(user)));
        return Result.success(findFriendUser);
    }


    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO userRegister) {
        userService.registerUser(userRegister);
        return Result.success();
    }

    @PostMapping("/getCode")
    public Result<Void> getCode(@RequestBody VerificationCodeDTO verificationCode) {
        retrievePasswordService.getCode(verificationCode);
        return Result.success();
    }

    @PostMapping("/retrievePassword")
    public Result<Void> retrievePassword(@RequestBody RetrievePasswordDTO retrievePasswordDTO) {
        retrievePasswordService.retrievePassword(retrievePasswordDTO);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody UserUpdateDTO userUpdate) {
        apiUserService.update(userUpdate);
        return Result.success();
    }


    @PostMapping("/updatePwd")
    public Result<Void> updatePwd(String pwd, String npwd) {
        userService.updatePwd(pwd, npwd);
        return Result.success();
    }
}
