package com.example.controller;

import com.example.dto.NewsDTO;
import com.example.service.NewsService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<String> deleteNewsById(@PathVariable Long id) {
        boolean isDeleted = service.deleteNewsById(id);
        if (isDeleted) {
            return ResponseEntity.ok("News with ID " + id + " has been deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("News with ID " + id + " not found.");
        }
    }

    @DeleteMapping("/by-keyword/{keyword}")
    public ResponseEntity<String> deleteNewsByKeyword(@PathVariable String keyword) {
        boolean isDeleted = service.deleteNewsByKeyword(keyword);
        if (isDeleted) {
            return ResponseEntity.ok("News with keyword \"" + keyword + "\" has been deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("No news found with keyword \"" + keyword + "\".");
        }
    }
}
