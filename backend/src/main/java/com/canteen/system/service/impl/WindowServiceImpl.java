package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.entity.Canteen;
import com.canteen.system.entity.Dish;
import com.canteen.system.entity.Window;
import com.canteen.system.mapper.CanteenMapper;
import com.canteen.system.mapper.DishMapper;
import com.canteen.system.mapper.WindowMapper;
import com.canteen.system.service.WindowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WindowServiceImpl extends ServiceImpl<WindowMapper, Window> implements WindowService {

    private final CanteenMapper canteenMapper;
    private final DishMapper dishMapper;

    @Override
    public List<Window> listByCanteenId(Long canteenId) {
        return this.list(new LambdaQueryWrapper<Window>()
                .eq(Window::getCanteenId, canteenId)
                .orderByAsc(Window::getFloor)
                .orderByAsc(Window::getSortOrder));
    }

    @Override
    public List<Window> listByCanteenIdAndFloor(Long canteenId, Integer floor) {
        return this.list(new LambdaQueryWrapper<Window>()
                .eq(Window::getCanteenId, canteenId)
                .eq(Window::getFloor, floor)
                .orderByAsc(Window::getSortOrder));
    }

    @Override
    public List<Integer> getFloorsByCanteenId(Long canteenId) {
        List<Window> windows = this.list(new LambdaQueryWrapper<Window>()
                .eq(Window::getCanteenId, canteenId)
                .select(Window::getFloor));
        return windows.stream()
                .map(Window::getFloor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public void addWindow(Window window) {
        Canteen canteen = canteenMapper.selectById(window.getCanteenId());
        if (canteen == null) {
            throw new RuntimeException("食堂不存在");
        }
        if (window.getFloor() == null || window.getFloor() < 1) {
            throw new RuntimeException("楼层号不合法");
        }
        if (canteen.getFloorCount() != null && window.getFloor() > canteen.getFloorCount()) {
            throw new RuntimeException("楼层号超过食堂总楼层数");
        }
        long count = this.count(new LambdaQueryWrapper<Window>()
                .eq(Window::getCanteenId, window.getCanteenId())
                .eq(Window::getFloor, window.getFloor()));
        if (count >= 10) {
            throw new RuntimeException("该楼层窗口数已达上限（10个）");
        }
        this.save(window);
    }

    @Override
    public void updateWindow(Window window) {
        Window existing = this.getById(window.getId());
        if (existing == null) {
            throw new RuntimeException("窗口不存在");
        }
        this.updateById(window);
    }

    @Override
    public void deleteWindow(Long id) {
        long dishCount = dishMapper.selectCount(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getWindowId, id));
        if (dishCount > 0) {
            throw new RuntimeException("该窗口下有" + dishCount + "个菜品，请先移除菜品");
        }
        this.removeById(id);
    }
}
