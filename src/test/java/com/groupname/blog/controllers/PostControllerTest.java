package com.groupname.blog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupname.blog.domain.entities.Post;
import com.groupname.blog.domain.entities.User;
import com.groupname.blog.domain.dtos.PostDto;
import com.groupname.blog.domain.dtos.CreatePostRequestDto;
import com.groupname.blog.domain.dtos.UpdatePostRequestDto;
import com.groupname.blog.services.PostService;
import com.groupname.blog.services.UserService;
import com.groupname.blog.mappers.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("PostController Unit Tests")
class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;
    private UUID postId;
    private UUID userId;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize MockMvc with the PostController
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        objectMapper = new ObjectMapper();

        // Sample Data
        postId = UUID.randomUUID();
        userId = UUID.randomUUID();

        post = new Post();
        post.setId(postId);
        post.setTitle("Sample Post");

        postDto = new PostDto();
        postDto.setId(postId);
        postDto.setTitle("Sample Post");
    }

    @Test
    @DisplayName("Get all posts with optional category and tag")
    void testGetAllPosts() throws Exception {
        // Mock service response
        when(postService.getAllPosts(null, null)).thenReturn(List.of(post));
        when(postMapper.toDto(any())).thenReturn(postDto);

        // Perform GET request
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Sample Post"));

        // Verify interactions
        verify(postService, times(1)).getAllPosts(null, null);
        verify(postMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Get draft posts for a user")
    void testGetDrafts() throws Exception {
        // Mock service response
        when(userService.getUserById(userId)).thenReturn(new User());
        when(postService.getDraftPosts(any())).thenReturn(List.of(post));
        when(postMapper.toDto(any())).thenReturn(postDto);

        // Perform GET request
        mockMvc.perform(get("/api/v1/posts/drafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Sample Post"));

        // Verify interactions
        verify(postService, times(1)).getDraftPosts(any());
        verify(postMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Create a new post")
    void testCreatePost() throws Exception {
        // Mock input and service response
        CreatePostRequestDto createRequest = new CreatePostRequestDto();
        createRequest.setTitle("New Sample Post");

        when(postService.createPost(any(), any())).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        // Send POST request
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .requestAttr("userId", userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sample Post"));

        // Verify interactions
        verify(postService, times(1)).createPost(any(), any());
        verify(postMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Update an existing post")
    void testUpdatePost() throws Exception {
        // Mock input and service response
        UpdatePostRequestDto updateRequest = new UpdatePostRequestDto();
        updateRequest.setTitle("Updated Title");

        when(postService.updatePost(eq(postId), any())).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        // Perform PUT request
        mockMvc.perform(put("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Post"));

        // Verify interactions
        verify(postService, times(1)).updatePost(eq(postId), any());
        verify(postMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Get a post by ID")
    void testGetPost() throws Exception {
        // Mock service response
        when(postService.getPost(postId)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        // Perform GET request
        mockMvc.perform(get("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Post"));

        // Verify interactions
        verify(postService, times(1)).getPost(postId);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    @DisplayName("Delete a post by ID")
    void testDeletePost() throws Exception {
        // Perform DELETE request
        mockMvc.perform(delete("/api/v1/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify interactions
        verify(postService, times(1)).deletePost(postId);
    }
}
