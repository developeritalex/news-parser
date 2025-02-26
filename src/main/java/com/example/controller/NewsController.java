package com.example.controller;

import com.example.dto.NewsDTO;
import com.example.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/news")
@RestController
public class NewsController {
    private final NewsService service;

    @GetMapping("/all")
    public List<NewsDTO> getAllNews() {
        return service.getAllNews();
    }

    @GetMapping("/today")
    public List<NewsDTO> getNewsToday() {
        return service.getNewsToday();
    }

    @GetMapping("/yesterday")
    public List<NewsDTO> getNewsYesterday() {
        return service.getNewsYesterday();
    }

    @GetMapping("/keywords")
    public List<NewsDTO> getNewsByKeyword(@RequestParam String keyword) { //Регистр важен
        return service.getNewsByKeyword(keyword);
    }

    @DeleteMapping("/{id}")
    public void deleteNewsById(@PathVariable Long id) {
        service.deleteNewsById(id);
    }

    @DeleteMapping("/by-keyword/{keyword}")
    public void deleteNewsByKeyword(@PathVariable String keyword) {
        service.deleteNewsByKeyword(keyword);
    }

    @PostMapping()
    public ResponseEntity<NewsDTO> addNews(@RequestBody NewsDTO newsDTO) {
        NewsDTO addedNews = service.addNews(newsDTO);
        return new ResponseEntity<>(addedNews, HttpStatus.CREATED);
    }
}
