package com.tma.digital_blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateArticleDTO {

    @NotNull
    private String content;
    @NotNull
    private String title;

}
