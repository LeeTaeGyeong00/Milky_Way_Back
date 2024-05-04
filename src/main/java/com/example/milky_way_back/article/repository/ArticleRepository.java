package com.example.milky_way_back.article.repository;

import com.example.milky_way_back.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByTitleContaining(String searchKeyword, Pageable pageable);
}
