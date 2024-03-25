package com.tma.digital_blog.controller;

import com.tma.digital_blog.dto.*;
import com.tma.digital_blog.service.ArticleService;
import com.tma.digital_blog.service.CommentService;
import com.tma.digital_blog.util.ReferencedException;
import com.tma.digital_blog.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {

    private ArticleService articleService;
    private CommentService commentService;

    @GetMapping("")
    @ApiResponse(responseCode = "201")
    @SecurityRequirements()
    public ResponseEntity<Page<ArticleDTO>> readArticles(
            @RequestParam(
                    value = "page",
                    defaultValue = "0",
                    required = false
            ) Integer page,
            @RequestParam(
                    value = "size",
                    defaultValue = "5",
                    required = false
            ) Integer size
    ) {
        Page<ArticleDTO> articles = articleService.findAll(page, size);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteArticle(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = articleService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createArticle(
            @RequestBody @Valid final CreateArticleDTO createArticleDTO,
            Authentication authentication
    ) {
        final Long createdId = articleService.create(
                createArticleDTO, authentication.getName()
        );
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/comments")
    @ApiResponse(responseCode = "201")
    @SecurityRequirements()
    public ResponseEntity<Long> createCommentForArticle(
            CreateCommentDTO createCommentDTO,
            Authentication authentication,
            @PathVariable Long id) {
        final Long createdId = commentService.create(
                createCommentDTO, id, authentication != null ? authentication.getName() : ""
        );
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/comments")
    @ApiResponse(responseCode = "201")
    @SecurityRequirements()
    public ResponseEntity<List<CommentDTO>> getCommentsOfArticle(
            @PathVariable Long id) {
        return new ResponseEntity<>(commentService.findAllByArticleId(id), HttpStatus.OK);
    }
}
