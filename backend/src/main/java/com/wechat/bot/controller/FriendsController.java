package com.wechat.bot.controller;

import com.wechat.bot.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alex
 * @since 2025/3/27 16:29
 * <p></p>
 */
@RequestMapping("friends")
@RestController()
public class FriendsController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/sync")
    public void syncContacts() {

        friendService.syncContacts();
    }

}
