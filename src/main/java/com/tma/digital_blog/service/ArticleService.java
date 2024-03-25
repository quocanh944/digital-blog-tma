package com.tma.digital_blog.service;

import com.tma.digital_blog.dto.ArticleDTO;
import com.tma.digital_blog.dto.CreateArticleDTO;
import com.tma.digital_blog.util.ReferencedWarning;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleService {
    public Page<ArticleDTO> findAll(Integer page, Integer size);
    public Long create(final CreateArticleDTO createArticleDTO, String username);
    public void delete(final Long id);
    public ReferencedWarning getReferencedWarning(final Long id);
}
