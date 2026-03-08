package com.canteen.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.PageResult;
import com.canteen.system.dto.Result;
import com.canteen.system.entity.User;
import com.canteen.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RequireAdmin(message = "需要管理员权限才能访问用户管理")
public class UserController {
    
    private final UserMapper userMapper;
    
    @GetMapping
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<User> page = userMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<User>()
                        .orderByDesc(User::getCreatedTime)
        );
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }
}
