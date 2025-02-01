package io.goorm.youtube.controller;

import io.goorm.youtube.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    private MessageSource messageSource;

    private final VideoService videoService;

    @Autowired
    public IndexController(VideoService videoService) {
        this.videoService = videoService;
    }

    //메인화면
    @GetMapping("")
    public String  index(Model model) {

        String message = messageSource.getMessage("login.button", null, Locale.getDefault());

        log.debug("message",message);

        model.addAttribute("videos", videoService.findIndex());

        return "index";
    }

    @GetMapping("/errorPage")
    public String errorPage(Model model) {

        log.debug("errorPage");

        return "errorPage";
    }

}
