package com.softuni.commentsrecipes.service;

import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;

import java.util.List;

public interface CommentRestService {
    CommentDto addComment(Long recipeId, CommentDto commentDto);

    List<Comment> findAllComments();

    void deleteComment(Long commentId, Long userId);

    Object getLastComment();
}
