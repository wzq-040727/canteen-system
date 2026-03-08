package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public Canteen getById(Long id) {
        return this.getOne(new LambdaQueryWrapper<Canteen>()
                .eq(Canteen::getId, id)
                .eq(Canteen::getStatus, 1));
    }
}
