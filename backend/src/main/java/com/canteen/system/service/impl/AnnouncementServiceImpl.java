package com.canteen.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.system.entity.Announcement;
import com.canteen.system.entity.Canteen;
import com.canteen.system.mapper.AnnouncementMapper;
import com.canteen.system.mapper.CanteenMapper;
import com.canteen.system.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private final CanteenMapper canteenMapper;

    @Override
    public List<Announcement> getValidByCanteenId(Long canteenId) {
        LocalDateTime now = LocalDateTime.now();
        return this.list(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getCanteenId, canteenId)
                .and(w -> w.isNull(Announcement::getStartTime).or().le(Announcement::getStartTime, now))
                .and(w -> w.isNull(Announcement::getEndTime).or().ge(Announcement::getEndTime, now))
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreatedTime));
    }

    @Override
    public List<Announcement> getAllValid() {
        LocalDateTime now = LocalDateTime.now();
        List<Announcement> list = this.list(new LambdaQueryWrapper<Announcement>()
                .and(w -> w.isNull(Announcement::getStartTime).or().le(Announcement::getStartTime, now))
                .and(w -> w.isNull(Announcement::getEndTime).or().ge(Announcement::getEndTime, now))
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreatedTime));
        // 填充食堂名称
        List<Long> canteenIds = list.stream()
                .map(Announcement::getCanteenId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (!canteenIds.isEmpty()) {
            Map<Long, String> nameMap = canteenMapper.selectBatchIds(canteenIds).stream()
                    .collect(Collectors.toMap(Canteen::getId, Canteen::getName));
            list.forEach(a -> a.setCanteenName(nameMap.get(a.getCanteenId())));
        }
        return list;
    }

    @Override
    public void addAnnouncement(Announcement announcement) {
        this.save(announcement);
    }

    @Override
    public void updateAnnouncement(Announcement announcement) {
        Announcement existing = this.getById(announcement.getId());
        if (existing == null) {
            throw new RuntimeException("公告不存在");
        }
        this.updateById(announcement);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        this.removeById(id);
    }
}
