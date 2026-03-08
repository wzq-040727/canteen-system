package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.entity.Window;
import com.canteen.system.mapper.WindowMapper;
import com.canteen.system.service.WindowService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WindowServiceImpl extends ServiceImpl<WindowMapper, Window> implements WindowService {
    
    @Override
    public List<Window> listByCanteenId(Long canteenId) {
        return this.list(new LambdaQueryWrapper<Window>()
                .eq(Window::getCanteenId, canteenId)
                .eq(Window::getStatus, 1)
                .orderByAsc(Window::getSortOrder));
    }
}
