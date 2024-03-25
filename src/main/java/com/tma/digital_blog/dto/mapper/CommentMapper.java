package com.tma.digital_blog.dto.mapper;

import com.tma.digital_blog.dto.CommentDTO;
import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.Comment;
import com.tma.digital_blog.model.User;
import com.tma.digital_blog.repository.ArticleRepository;
import com.tma.digital_blog.repository.UserRepository;
import com.tma.digital_blog.util.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private static UserRepository userRepository;
    private static ArticleRepository articleRepository;

    public static CommentDTO mapToDTO(final Comment comment, final CommentDTO commentDTO) {
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setUsersId(comment.getUser() == null ? null : comment.getUser().getId());
        commentDTO.setArticelId(comment.getArticle() == null ? null : comment.getArticle().getId());
        return commentDTO;
    }

    public static Comment mapToEntity(final CommentDTO commentDTO, final Comment comment) {
        comment.setContent(commentDTO.getContent());
        final User usersId = commentDTO.getUsersId() == null ? null : userRepository.findById(commentDTO.getUsersId())
                .orElseThrow(() -> new NotFoundException("usersId not found"));
        comment.setUser(usersId);
        final Article articleId = commentDTO.getArticelId() == null ? null : articleRepository.findById(commentDTO.getArticelId())
                .orElseThrow(() -> new NotFoundException("articleId not found"));
        comment.setArticle(articleId);
        return comment;
    }
}
