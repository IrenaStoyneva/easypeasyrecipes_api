package com.softuni.commentsrecipes.web;

import com.softuni.commentsrecipes.Repository.CommentRepository;
import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;
import com.softuni.commentsrecipes.model.entity.Recipe;
import com.softuni.commentsrecipes.model.entity.User;
import com.softuni.commentsrecipes.service.CommentRestService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {
    private final CommentRestService commentRestService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(CommentRestController.class);

    public CommentRestController(CommentRestService commentRestService, ModelMapper modelMapper) {
        this.commentRestService = commentRestService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> commentDtos = commentRestService.findAllComments().stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDtos);
    }

    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long recipeId, @RequestBody CommentDto commentDto) {
        commentRestService.addComment(recipeId, commentDto);
        CommentDto savedCommentDto = modelMapper.map(commentRestService.getLastComment(), CommentDto.class);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCommentDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedCommentDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentRestService.deleteComment(id, null); // Update to provide userId if necessary
        return ResponseEntity.noContent().build();
    }
}
