package com.canteen.system.controller;

import com.canteen.system.dto.*;
import com.canteen.system.entity.User;
import com.canteen.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }
    
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }
    
    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }
    
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        userService.updateUserInfo(user);
        return Result.success();
    }
    
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.updatePassword(oldPassword, newPassword);
        return Result.success();
    }
}
