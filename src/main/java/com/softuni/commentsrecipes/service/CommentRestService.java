package com.softuni.commentsrecipes.service;

import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRestService {
    CommentDto addComment(Long recipeId, CommentDto commentDto);

    List<Comment> findAllComments();

    void deleteComment(UserDetails userDetails, Long userId);

    Object getLastComment();

    CommentDto updateComment(UserDetails userDetails,Long commentId, CommentDto commentDto);

    Comment getCommentById( Long id);
}
