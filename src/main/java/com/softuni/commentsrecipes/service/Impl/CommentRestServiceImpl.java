package com.softuni.commentsrecipes.service.Impl;


import com.softuni.commentsrecipes.repository.CommentRepository;
import com.softuni.commentsrecipes.repository.RecipeRepository;
import com.softuni.commentsrecipes.repository.UserRepository;
import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;
import com.softuni.commentsrecipes.model.entity.Recipe;
import com.softuni.commentsrecipes.model.entity.User;
import com.softuni.commentsrecipes.service.CommentRestService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentRestServiceImpl implements CommentRestService {

    private static final Logger logger = LoggerFactory.getLogger(CommentRestServiceImpl.class);
    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentRestServiceImpl(CommentRepository commentRepository, RecipeRepository recipeRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CommentDto addComment(Long recipeId, CommentDto commentDto) {
        logger.info("Adding comment to recipe ID: {}", recipeId);
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            logger.error("Recipe with ID {} not found", recipeId);
            throw new IllegalArgumentException("Recipe not found");
        }

        Optional<User> userOptional = userRepository.findById(commentDto.getAuthorId());
        if (userOptional.isEmpty()) {
            logger.error("User with ID {} not found", commentDto.getAuthorId());
            throw new IllegalArgumentException("User not found");
        }

        Recipe recipe = recipeOptional.get();
        User user = userOptional.get();

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setCreatedOn(LocalDateTime.now());
        comment.setRecipe(recipe);
        comment.setAuthor(user);

        Comment savedComment = commentRepository.save(comment);
        return modelMapper.map(savedComment, CommentDto.class);
    }

    @Override
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    @Override
    @PreAuthorize("@commentRestServiceImpl.isOwner(#userDetails, #commentId) or hasRole('ROLE_ADMIN')")
    public void deleteComment(UserDetails userDetails, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        commentRepository.delete(comment);
    }
    public boolean isOwner(UserDetails userDetails, Long commentId) {
        return commentRepository.findById(commentId)
                .map(comment -> comment.getAuthor().getUsername().equals(userDetails.getUsername()))
                .orElse(false);
    }
    @Override
    public Comment getLastComment() {
        return commentRepository.findTopByOrderByIdDesc();
    }
    @Override
    public CommentDto updateComment(@AuthenticationPrincipal UserDetails userDetails, Long commentId, CommentDto commentDto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment is not found"));

        comment.setContent(commentDto.getContent());
        comment.setCreatedOn(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        return modelMapper.map(updatedComment, CommentDto.class);
    }
    @Override
    public Comment getCommentById(Long id) {
        logger.info("Finding comment with ID: {}", id);
        return commentRepository.findById(id).orElse(null);
    }

}