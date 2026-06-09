package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.dto.CanteenManageUpdateDTO;
import com.canteen.system.entity.Canteen;
import com.canteen.system.mapper.CanteenMapper;
import com.canteen.system.service.CanteenService;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CanteenServiceImpl extends ServiceImpl<CanteenMapper, Canteen> implements CanteenService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<Canteen> listAll() {
        List<Canteen> list = this.list(new LambdaQueryWrapper<Canteen>()
                .orderByAsc(Canteen::getId));
        list.forEach(this::applyEffectiveStatus);
        return list;
    }

    @Override
    public List<Canteen> listForManage() {
        return this.list(new LambdaQueryWrapper<Canteen>()
                .orderByAsc(Canteen::getId));
    }

    @Override
    public Canteen findById(Long id) {
        Canteen canteen = this.getById(id);
        if (canteen != null) {
            applyEffectiveStatus(canteen);
        }
        return canteen;
    }

    /**
     * 根据当前时间和营业时间动态计算营业状态。
     * status=0 表示管理员手动关闭，直接显示休息中；
     * status=1 表示按营业时间自动判断。
     */
    private void applyEffectiveStatus(Canteen canteen) {
        if (canteen.getStatus() == null || canteen.getStatus() == 0) {
            canteen.setStatus(0);
            return;
        }
        String hours = canteen.getOpeningHours();
        if (hours == null || !hours.contains("-")) {
            return; // 无法解析，保留 status=1
        }
        try {
            String[] parts = hours.split("-");
            LocalTime open = LocalTime.parse(parts[0].trim(), TIME_FMT);
            LocalTime close = LocalTime.parse(parts[1].trim(), TIME_FMT);
            LocalTime now = LocalTime.now();
            if (now.isBefore(open) || now.isAfter(close)) {
                canteen.setStatus(0); // 不在营业时间内
            }
        } catch (Exception e) {
            // 解析失败，保留原 status
        }
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
            if (dto.getFloorCount() != null) {
                if (dto.getFloorCount() < 1) {
                    throw new RuntimeException("楼层数不能小于1");
                }
                canteen.setFloorCount(dto.getFloorCount());
            }
        } else {
            throw new RuntimeException("无权限操作食堂信息");
        }

        this.updateById(canteen);
    }
}
