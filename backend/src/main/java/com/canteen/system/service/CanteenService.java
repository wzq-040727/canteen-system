package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.dto.CanteenManageUpdateDTO;
import com.canteen.system.entity.Canteen;
import java.util.List;

public interface CanteenService extends IService<Canteen> {
    List<Canteen> listAll();
    List<Canteen> listForManage();
    Canteen findById(Long id);
    void updateByRole(CanteenManageUpdateDTO dto, Integer role);
}
