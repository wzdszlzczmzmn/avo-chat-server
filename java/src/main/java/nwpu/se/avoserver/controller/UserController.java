package nwpu.se.avoserver.controller;


import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nwpu.se.avoserver.common.CommonUtil;
import nwpu.se.avoserver.common.JwtUtil;
import nwpu.se.avoserver.common.RedisUtil;
import nwpu.se.avoserver.constant.ResultCodeEnum;
import nwpu.se.avoserver.entity.User;
import nwpu.se.avoserver.exception.BusinessException;
import nwpu.se.avoserver.param.GetUserInfoParam;
import nwpu.se.avoserver.param.LoginParam;
import nwpu.se.avoserver.param.RegisterParam;
import nwpu.se.avoserver.protocol.PipeOutputBean;
import nwpu.se.avoserver.service.UserService;
import nwpu.se.avoserver.vo.RegisterVO;
import nwpu.se.avoserver.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.ObjLongConsumer;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private PipeOutputBean pipeOutputBean;

    @PostMapping("/api/user/login")
    public Object login(@RequestBody @Valid LoginParam loginParam, HttpServletResponse response) {
        User user = userService.login(loginParam);
        String token = jwtUtil.getTokenFromUser(user);
        if(RedisUtil.KeyOps.hasKey(token)){
            throw new BusinessException(ResultCodeEnum.REPEAT_OPERATION, "用户已登录");
        }
        RedisUtil.HashOps.hPut(token, "login", "1");
        RedisUtil.HashOps.hPut(user.getUserId().toString(), "token", token);
        response.setHeader("token", token);
        return new Object();
    }

    @PostMapping("/api/user/logout")
    public Object logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        User user = jwtUtil.getUserFromToken(token);
        if(!RedisUtil.KeyOps.hasKey(token)){
            throw new BusinessException(ResultCodeEnum.REPEAT_OPERATION, "用户未登录");
        }
        pipeOutputBean.logOut(token);
        RedisUtil.KeyOps.delete(user.getUserId().toString());
        return new Object();
    }

    @PostMapping("/api/user/register")
    public RegisterVO register(@RequestBody @Valid RegisterParam registerParam) {
        int userID =  userService.register(registerParam);

        return new RegisterVO(userID);
    }

    @GetMapping("/test/notifyP2P")
    public Object testNotify() {
        pipeOutputBean.notifyP2P(12987774,97948661);
        return new Object();
    }

    @GetMapping("/test/get")
    public RegisterVO testGet(@Valid RegisterParam registerParam, HttpServletResponse response){
        System.out.println(registerParam);
        User user = new User();
        user.setUserId(12345678);
        String token = jwtUtil.getTokenFromUser(user);
        response.setHeader("token", token);
        return new RegisterVO(12345678);
    }

    @GetMapping("/api/user/info")
    public UserInfoVO getUserInfo(@Valid GetUserInfoParam getUserInfoParam) {
        return userService.getUserInfo(getUserInfoParam);
    }



}
