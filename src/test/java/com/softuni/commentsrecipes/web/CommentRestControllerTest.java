package com.softuni.commentsrecipes.web;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softuni.commentsrecipes.model.dto.CommentDto;
import com.softuni.commentsrecipes.model.entity.Comment;
import com.softuni.commentsrecipes.service.CommentRestService;
import com.softuni.commentsrecipes.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@MockBean(JwtService.class)
class CommentRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private CommentRestService commentRestService;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("This is a test comment");
        commentDto.setAuthorId(1L);
    }

    @Test
    void getAllComments_ShouldReturnComments() throws Exception {
        when(commentRestService.findAllComments()).thenReturn(Collections.singletonList(new Comment()));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createComment_ShouldCreateAndReturnComment() throws Exception {

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("This is a test comment");

        CommentDto savedCommentDto = modelMapper.map(savedComment, CommentDto.class);

        when(commentRestService.addComment(anyLong(), any(CommentDto.class))).thenReturn(savedCommentDto);
        when(commentRestService.getLastComment()).thenReturn(savedComment);

        mockMvc.perform(post("/api/comments/recipe/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedCommentDto.getId()))
                .andExpect(jsonPath("$.content").value(savedCommentDto.getContent()));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateComment_ShouldUpdateAndReturnComment() throws Exception {
        when(commentRestService.updateComment(any(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteComment_ShouldDeleteComment() throws Exception {
        Mockito.doNothing().when(commentRestService).deleteComment(any(), anyLong());

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCommentById_ShouldReturnComment() throws Exception {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test content");

        when(commentRestService.getCommentById(anyLong())).thenReturn(comment);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test content"));
    }
}
