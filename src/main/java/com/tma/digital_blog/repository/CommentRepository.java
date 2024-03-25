package com.tma.digital_blog.repository;

import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.Comment;
import com.tma.digital_blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment findFirstByUser(User user);

    Comment findFirstByArticle(Article article);

    List<Comment> findAllByArticleId(Long articleId);

}
