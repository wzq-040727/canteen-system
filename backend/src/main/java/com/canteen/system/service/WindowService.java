package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.entity.Window;
import java.util.List;

public interface WindowService extends IService<Window> {
    List<Window> listByCanteenId(Long canteenId);
}
