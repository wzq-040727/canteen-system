package com.canteen.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.system.entity.Announcement;
import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    List<Announcement> getValidByCanteenId(Long canteenId);
    List<Announcement> getAllValid();
    void addAnnouncement(Announcement announcement);
    void updateAnnouncement(Announcement announcement);
    void deleteAnnouncement(Long id);
}
