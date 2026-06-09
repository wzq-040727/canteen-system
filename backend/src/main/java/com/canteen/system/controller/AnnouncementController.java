package com.canteen.system.controller;

import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.dto.Result;
import com.canteen.system.entity.Announcement;
import com.canteen.system.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<List<Announcement>> getAllValid() {
        return Result.success(announcementService.getAllValid());
    }

    @GetMapping("/canteen/{canteenId}")
    public Result<List<Announcement>> getByCanteen(@PathVariable Long canteenId) {
        return Result.success(announcementService.getValidByCanteenId(canteenId));
    }

    @PostMapping
    @RequireAdmin(message = "需要管理员权限才能发布公告")
    public Result<Void> add(@RequestBody Announcement announcement) {
        announcementService.addAnnouncement(announcement);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequireAdmin(message = "需要管理员权限才能编辑公告")
    public Result<Void> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementService.updateAnnouncement(announcement);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireAdmin(message = "需要管理员权限才能删除公告")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }
}
