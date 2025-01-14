package nwpu.se.avoserver.service;

import nwpu.se.avoserver.entity.User;
import nwpu.se.avoserver.param.GetUserInfoParam;
import nwpu.se.avoserver.param.LoginParam;
import nwpu.se.avoserver.param.RegisterParam;
import nwpu.se.avoserver.vo.UserInfoVO;

/**
* @author xiaoheng
* @description 针对表【t_user(user table)】的数据库操作Service
* @createDate 2022-12-11 13:42:00
*/
public interface UserService {

    User login(LoginParam loginParam);

    int register(RegisterParam registerParam);

    UserInfoVO getUserInfo(GetUserInfoParam getUserInfoParam);
}
