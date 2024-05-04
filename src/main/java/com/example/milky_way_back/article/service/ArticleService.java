package com.example.milky_way_back.article.service;

import com.example.milky_way_back.article.DTO.AddArticle;
import com.example.milky_way_back.article.entity.Article;
import com.example.milky_way_back.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    //게시글 등록
    public Article save(AddArticle request){
        return articleRepository.save(request.toEntity());
    }
    //게시글 조회
    public List<Article> findAll(Pageable pageable){
        return articleRepository.findAll(pageable).getContent();
    }
    public Article findById (long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }
    public void delete(long id){
        articleRepository.deleteById(id);
    }
//    @Transactional
//    public Article update(long id, UpdateRequest request) {
//        Article article = articleRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found: "+id));
//
//        article.update(request.getTitle(), request.getContent());
//        return article;
//    } 수정 넣을건가요?

}
