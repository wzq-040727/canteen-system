package com.canteen.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.dto.*;
import com.canteen.system.entity.User;
import com.canteen.system.mapper.UserMapper;
import com.canteen.system.service.UserService;
import com.canteen.system.util.JwtUtil;
import com.canteen.system.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setToken(token);
        return vo;
    }
    
    @Override
    public void register(RegisterDTO registerDTO) {
        Long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, registerDTO.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        if (registerDTO.getStudentId() != null && !registerDTO.getStudentId().isEmpty()) {
            count = this.count(new LambdaQueryWrapper<User>()
                    .eq(User::getStudentId, registerDTO.getStudentId()));
            if (count > 0) {
                throw new RuntimeException("学号已被注册");
            }
        }
        
        if (registerDTO.getPhone() != null && !registerDTO.getPhone().isEmpty()) {
            count = this.count(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, registerDTO.getPhone()));
            if (count > 0) {
                throw new RuntimeException("手机号已被注册");
            }
        }
        
        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setRole(0);
        user.setStatus(1);
        this.save(user);
    }
    
    @Override
    public User getCurrentUser() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
    
    @Override
    public void updateUserInfo(User user) {
        User currentUser = getCurrentUser();
        currentUser.setRealName(user.getRealName());
        currentUser.setEmail(user.getEmail());
        currentUser.setAvatar(user.getAvatar());
        this.updateById(currentUser);
    }
    
    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        User user = getCurrentUser();
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        this.updateById(user);
    }
}
