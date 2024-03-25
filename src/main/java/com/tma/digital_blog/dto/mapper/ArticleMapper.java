package com.tma.digital_blog.dto.mapper;

import com.tma.digital_blog.dto.ArticleDTO;
import com.tma.digital_blog.dto.CreateArticleDTO;
import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.User;
import com.tma.digital_blog.repository.UserRepository;
import com.tma.digital_blog.util.NotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    private static UserRepository userRepository;

    public static ArticleDTO mapToDTO(final Article article, final ArticleDTO articleDTO) {
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setCreatedBy(article.getCreatedBy() == null ? null : article.getCreatedBy().getId());
        return articleDTO;
    }

    public static Article mapToEntity(final ArticleDTO articleDTO, final Article article) {
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        final User createdBy = articleDTO.getCreatedBy() == null ? null : userRepository.findById(articleDTO.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("createdBy not found"));
        article.setCreatedBy(createdBy);
        return article;
    }
}
