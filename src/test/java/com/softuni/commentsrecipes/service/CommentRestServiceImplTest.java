package com.softuni.commentsrecipes.service;
import com.softuni.commentsrecipes.repository.CommentRepository;
import com.softuni.commentsrecipes.repository.RecipeRepository;
import com.softuni.commentsrecipes.repository.UserRepository;
import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;
import com.softuni.commentsrecipes.model.entity.Recipe;
import com.softuni.commentsrecipes.model.entity.User;
import com.softuni.commentsrecipes.service.Impl.CommentRestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentRestServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentRestServiceImpl commentRestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_ShouldAddCommentWhenValid() {
        Long recipeId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        commentDto.setContent("Test comment");

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setContent("Test comment");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(commentDto.getAuthorId())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(modelMapper.map(any(Comment.class), eq(CommentDto.class))).thenReturn(commentDto);

        CommentDto result = commentRestService.addComment(recipeId, commentDto);

        assertNotNull(result);
        assertEquals("Test comment", result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowExceptionWhenRecipeNotFound() {
        Long recipeId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentRestService.addComment(recipeId, commentDto));
    }

    @Test
    void addComment_ShouldThrowExceptionWhenUserNotFound() {
        Long recipeId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(commentDto.getAuthorId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentRestService.addComment(recipeId, commentDto));
    }

    @Test
    void findAllComments_ShouldReturnAllComments() {
        List<Comment> comments = List.of(new Comment(), new Comment());

        when(commentRepository.findAll()).thenReturn(comments);

        List<Comment> result = commentRestService.findAllComments();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void deleteComment_ShouldDeleteCommentWhenUserIsOwner() {
        Long commentId = 1L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        Comment comment = new Comment();
        User user = new User();
        user.setUsername("testuser");
        comment.setAuthor(user);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentRestService.deleteComment(userDetails, commentId);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_ShouldThrowExceptionWhenCommentNotFound() {
        Long commentId = 1L;
        UserDetails userDetails = mock(UserDetails.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentRestService.deleteComment(userDetails, commentId));
    }

    @Test
    void updateComment_ShouldUpdateCommentWhenValid() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Updated content");

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Old content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(modelMapper.map(any(Comment.class), eq(CommentDto.class))).thenReturn(commentDto);

        CommentDto result = commentRestService.updateComment(mock(UserDetails.class), commentId, commentDto);

        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void getCommentById_ShouldReturnCommentWhenFound() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentRestService.getCommentById(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
    }

    @Test
    void getCommentById_ShouldReturnNullWhenNotFound() {
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        Comment result = commentRestService.getCommentById(commentId);

        assertNull(result);
    }

    @Test
    void isOwner_ShouldReturnTrueWhenUserIsOwner() {
        Long commentId = 1L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        Comment comment = new Comment();
        User user = new User();
        user.setUsername("testuser");
        comment.setAuthor(user);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        boolean result = commentRestService.isOwner(userDetails, commentId);

        assertTrue(result);
    }

    @Test
    void isOwner_ShouldReturnFalseWhenUserIsNotOwner() {
        Long commentId = 1L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        Comment comment = new Comment();
        User user = new User();
        user.setUsername("otheruser");
        comment.setAuthor(user);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        boolean result = commentRestService.isOwner(userDetails, commentId);

        assertFalse(result);
    }

    @Test
    void getLastComment_ShouldReturnLastComment() {
        Comment comment = new Comment();
        comment.setId(1L);

        when(commentRepository.findTopByOrderByIdDesc()).thenReturn(comment);

        Comment result = commentRestService.getLastComment();

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}