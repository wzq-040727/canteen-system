package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.dto.*;
import com.canteen.system.entity.User;

public interface UserService extends IService<User> {
    LoginVO login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
    User getCurrentUser();
    void updateUserInfo(User user);
    void updatePassword(String oldPassword, String newPassword);
}
