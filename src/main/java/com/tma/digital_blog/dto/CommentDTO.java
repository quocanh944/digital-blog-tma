package com.tma.digital_blog.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommentDTO {

    private Long id;
    private String content;
    private Long usersId;
    private Long articelId;

}
