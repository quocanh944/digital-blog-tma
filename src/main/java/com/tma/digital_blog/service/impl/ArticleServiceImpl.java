package com.tma.digital_blog.service.impl;

import com.tma.digital_blog.dto.CreateArticleDTO;
import com.tma.digital_blog.dto.mapper.ArticleMapper;
import com.tma.digital_blog.model.Article;
import com.tma.digital_blog.model.Comment;
import com.tma.digital_blog.dto.ArticleDTO;
import com.tma.digital_blog.model.User;
import com.tma.digital_blog.repository.ArticleRepository;
import com.tma.digital_blog.repository.CommentRepository;
import com.tma.digital_blog.repository.UserRepository;
import com.tma.digital_blog.service.ArticleService;
import com.tma.digital_blog.util.BadInputException;
import com.tma.digital_blog.util.NotFoundException;
import com.tma.digital_blog.util.ReferencedWarning;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ArticleDTO> findAll(Integer page, Integer size) {
        return articleRepository
                .findAll(PageRequest.of(page, size))
                .map(article -> ArticleMapper.mapToDTO(article, new ArticleDTO()));
    }

    @Override
    public Long create(
            final CreateArticleDTO articleDTO,
            String username
    ) {
        User u = userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Not found this user")
        );
        if (!u.isCanCreateArticle()) {
            throw new BadInputException("This user cannot create article");
        }
        Article article = new Article();
        article.setContent(articleDTO.getContent());
        article.setTitle(articleDTO.getTitle());
        article.setCreatedBy(u);

        return articleRepository.save(article).getId();
    }

    @Override
    public void delete(final Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Article article = articleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Comment articelIdComment = commentRepository.findFirstByArticle(article);
        if (articelIdComment != null) {
            referencedWarning.setKey("article.comment.articleId.referenced");
            referencedWarning.addParam(articelIdComment.getId());
            return referencedWarning;
        }
        return null;
    }

}
