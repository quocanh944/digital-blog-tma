package com.tma.digital_blog.service;

import com.tma.digital_blog.dto.CommentDTO;
import com.tma.digital_blog.dto.CreateCommentDTO;
import com.tma.digital_blog.model.Comment;

import java.util.List;

public interface CommentService {
    public List<CommentDTO> findAllByArticleId(Long articleId);
    public Long create(
            final CreateCommentDTO createCommentDTO,
            Long articleId,
            String username
    );
}
