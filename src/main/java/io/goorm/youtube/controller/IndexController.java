package io.goorm.youtube.controller;

import io.goorm.youtube.admin.VideoMainDTO;
import io.goorm.youtube.service.impl.VideoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api")
public class IndexController {

    @Autowired
    private MessageSource messageSource;

    private final VideoServiceImpl videoService;

    @Autowired
    public IndexController(VideoServiceImpl videoService) {
        this.videoService = videoService;
    }

    //메인화면
    @GetMapping("")
    public ResponseEntity<List<VideoMainDTO>> index() {

        return ResponseEntity.ok(videoService.findIndex());
    }


}
