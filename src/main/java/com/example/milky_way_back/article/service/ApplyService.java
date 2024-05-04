package com.example.milky_way_back.article.service;

import com.example.milky_way_back.article.entity.Apply;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.repository.ApplyRepository;
import com.example.milky_way_back.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ApplyService {
    private final ApplyRepository applyRepository;

    public List<Apply> findAll() {
        return applyRepository.findAll();
    }
}
