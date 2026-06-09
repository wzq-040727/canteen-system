package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.dto.CanteenManageUpdateDTO;
import com.canteen.system.entity.Canteen;
import com.canteen.system.mapper.CanteenMapper;
import com.canteen.system.service.CanteenService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CanteenServiceImpl extends ServiceImpl<CanteenMapper, Canteen> implements CanteenService {
    
    @Override
    public List<Canteen> listAll() {
        return this.list(new LambdaQueryWrapper<Canteen>()
                .eq(Canteen::getStatus, 1)
                .orderByAsc(Canteen::getId));
    }

    @Override
    public List<Canteen> listForManage() {
        return this.list(new LambdaQueryWrapper<Canteen>()
                .orderByAsc(Canteen::getId));
    }
    
    @Override
    public Canteen findById(Long id) {
        return this.getOne(new LambdaQueryWrapper<Canteen>()
                .eq(Canteen::getId, id)
                .eq(Canteen::getStatus, 1));
    }

    @Override
    public void updateByRole(CanteenManageUpdateDTO dto, Integer role) {
        if (dto == null || dto.getId() == null) {
            throw new RuntimeException("食堂ID不能为空");
        }
        Canteen canteen = this.getById(dto.getId());
        if (canteen == null) {
            throw new RuntimeException("食堂不存在");
        }

        if (role != null && role == 1) {
            if (dto.getOpeningHours() == null || dto.getOpeningHours().trim().isEmpty()) {
                throw new RuntimeException("营业时间不能为空");
            }
            canteen.setOpeningHours(dto.getOpeningHours().trim());
        } else if (role != null && role == 2) {
            if (dto.getOpeningHours() != null && !dto.getOpeningHours().trim().isEmpty()) {
                canteen.setOpeningHours(dto.getOpeningHours().trim());
            }
            if (dto.getStatus() != null) {
                if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                    throw new RuntimeException("营业状态参数不合法");
                }
                canteen.setStatus(dto.getStatus());
            }
        } else {
            throw new RuntimeException("无权限操作食堂信息");
        }

        this.updateById(canteen);
    }
}
