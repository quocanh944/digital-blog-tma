package com.tma.digital_blog.repository;

import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticleRepository extends JpaRepository<Article, Long> {

    Article findFirstByCreatedBy(User user);

}
