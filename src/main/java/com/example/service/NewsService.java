package com.example.service;

import com.example.dto.NewsDTO;
import java.util.List;

public interface NewsService {

    List<NewsDTO> getAllNews();
    List<NewsDTO> getNewsToday();
    List<NewsDTO> getNewsYesterday();
    List<NewsDTO> getNewsByKeyword(String keyword);
    boolean deleteNewsById(Long id);
    boolean deleteNewsByKeyword(String keyword);
}
