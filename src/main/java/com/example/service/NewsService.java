package com.example.service;

import com.example.dto.NewsDTO;
import java.util.List;

public interface NewsService {
    List<NewsDTO> getAllNews();
    List<NewsDTO> getNewsToday();
    List<NewsDTO> getNewsYesterday();
    List<NewsDTO> getNewsByKeyword(String keyword);
    void deleteNewsById(Long id);
    void deleteNewsByKeyword(String keyword);
    NewsDTO addNews(NewsDTO newsDTO);
}
