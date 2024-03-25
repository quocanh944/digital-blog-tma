package com.tma.digital_blog.service.impl;

import com.tma.digital_blog.dto.CreateCommentDTO;
import com.tma.digital_blog.dto.mapper.CommentMapper;
import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.Comment;
import com.tma.digital_blog.dto.CommentDTO;
import com.tma.digital_blog.model.User;
import com.tma.digital_blog.repository.ArticleRepository;
import com.tma.digital_blog.repository.CommentRepository;
import com.tma.digital_blog.repository.UserRepository;
import com.tma.digital_blog.service.CommentService;
import com.tma.digital_blog.util.BadInputException;
import com.tma.digital_blog.util.NotFoundException;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    public List<CommentDTO> findAllByArticleId(Long articleId) {
        final List<Comment> comments = commentRepository.findAllByArticleId(articleId);
        return comments.stream()
                .map(comment -> CommentMapper.mapToDTO(comment, new CommentDTO()))
                .toList();
    }

    @Override
    public Long create(
            CreateCommentDTO createCommentDTO,
            Long articleId,
            String username
    ) {
        User u = userRepository.findUserByUsername(username).orElse(null);
        Article article = articleRepository
                .findById(articleId)
                .orElseThrow(
                        () -> new BadInputException("Not found this article")
                );

        final Comment comment = new Comment();
        comment.setUser(u);
        comment.setContent(createCommentDTO.getContent());
        comment.setArticle(article);

        return commentRepository.save(comment).getId();
    }

}
