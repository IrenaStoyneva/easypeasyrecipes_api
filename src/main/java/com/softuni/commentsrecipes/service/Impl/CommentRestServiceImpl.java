package com.softuni.commentsrecipes.service.Impl;


import com.softuni.commentsrecipes.Repository.CommentRepository;
import com.softuni.commentsrecipes.Repository.RecipeRepository;
import com.softuni.commentsrecipes.Repository.UserRepository;
import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;
import com.softuni.commentsrecipes.model.entity.Recipe;
import com.softuni.commentsrecipes.model.entity.User;
import com.softuni.commentsrecipes.service.CommentRestService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        commentRepository.delete(comment);
    }
    @Override
    public Comment getLastComment() {
        return commentRepository.findTopByOrderByIdDesc();
    }

}