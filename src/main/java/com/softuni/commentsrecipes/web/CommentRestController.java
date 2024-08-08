package com.softuni.commentsrecipes.web;

import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;
import com.softuni.commentsrecipes.service.CommentRestService;
import com.softuni.commentsrecipes.service.JwtService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {
    private final CommentRestService commentRestService;
    private final ModelMapper modelMapper;private static final Logger logger = LoggerFactory.getLogger(CommentRestController.class);

    public CommentRestController(CommentRestService commentRestService, ModelMapper modelMapper, JwtService jwtService) {
        this.commentRestService = commentRestService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> comments = commentRestService.findAllComments().stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
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
    public ResponseEntity<CommentDto> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        commentRestService.deleteComment(userDetails, id);
        return ResponseEntity
                .noContent()
                .build();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentDto> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody CommentDto commentDto) {

        CommentDto updatedComment = commentRestService.updateComment(userDetails, id, commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        logger.info("Fetching comment with ID: {}", id);
        Comment comment = commentRestService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        return ResponseEntity.ok(commentDto);
    }
}