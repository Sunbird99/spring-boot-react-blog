package com.groupname.blog.services.impl;

import com.groupname.blog.domain.PostStatus;
import com.groupname.blog.domain.entities.Category;
import com.groupname.blog.domain.entities.Post;
import com.groupname.blog.domain.entities.Tag;
import com.groupname.blog.domain.entities.User;
import com.groupname.blog.domain.CreatePostRequest;
import com.groupname.blog.domain.UpdatePostRequest;
import com.groupname.blog.repositories.PostRepository;
import com.groupname.blog.services.CategoryService;
import com.groupname.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("PostServiceImpl Unit Tests")
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private Category category;
    private Tag tag;
    private User user;

    private UUID postId;
    private UUID categoryId;
    private UUID tagId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize sample data
        categoryId = UUID.randomUUID();
        tagId = UUID.randomUUID();
        postId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);
        category.setName("Technology");

        tag = new Tag();
        tag.setId(tagId);
        tag.setName("Tech");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("john_doe");

        post = new Post();
        post.setId(postId);
        post.setTitle("Sample Post");
        post.setContent("This is a sample post content.");
        post.setStatus(PostStatus.PUBLISHED);
        post.setCategory(category);
        post.setTags(Set.of(tag));
        post.setAuthor(user);
    }

    @Test
    @DisplayName("Get all posts with category and tag")
    void testGetAllPostsWithCategoryAndTag() {
        // Mock dependencies
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(tagService.getTagById(tagId)).thenReturn(tag);
        when(postRepository.findAllByStatusAndCategoryAndTagsContaining(PostStatus.PUBLISHED, category, tag))
                .thenReturn(List.of(post));

        // Call method
        List<Post> posts = postService.getAllPosts(categoryId, tagId);

        // Verify
        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Sample Post");
        verify(postRepository, times(1)).findAllByStatusAndCategoryAndTagsContaining(
                eq(PostStatus.PUBLISHED), eq(category), eq(tag)
        );
    }

    @Test
    @DisplayName("Get draft posts for a user")
    void testGetDraftPosts() {
        // Mock dependencies
        when(postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT)).thenReturn(List.of(post));

        // Call method
        List<Post> draftPosts = postService.getDraftPosts(user);

        // Verify
        assertThat(draftPosts).hasSize(1);
        assertThat(draftPosts.get(0).getAuthor()).isEqualTo(user);
        verify(postRepository, times(1)).findAllByAuthorAndStatus(eq(user), eq(PostStatus.DRAFT));
    }

    @Test
    @DisplayName("Create a new post")
    void testCreatePost() {
        // Prepare request
        CreatePostRequest createRequest = new CreatePostRequest();
        createRequest.setTitle("New Post");
        createRequest.setContent("Content for the new post.");
        createRequest.setCategoryId(categoryId);
        createRequest.setTagIds(Set.of(tagId));

        // Mock dependencies
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(tagService.getTagByIds(Set.of(tagId))).thenReturn(List.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Call method
        Post createdPost = postService.createPost(user, createRequest);

        // Verify
        assertThat(createdPost.getTitle()).isEqualTo("Sample Post");
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("Update an existing post")
    void testUpdatePost() {
        // Prepare request
        UpdatePostRequest updateRequest = new UpdatePostRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");
        updateRequest.setCategoryId(categoryId);
        updateRequest.setTagIds(Set.of(tagId));

        // Mock dependencies
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(tagService.getTagByIds(Set.of(tagId))).thenReturn(List.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Call method
        Post updatedPost = postService.updatePost(postId, updateRequest);

        // Verify
        assertThat(updatedPost.getTitle()).isEqualTo("Sample Post");
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("Get a post by ID")
    void testGetPost() {
        // Mock dependencies
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Call method
        Post retrievedPost = postService.getPost(postId);

        // Verify
        assertThat(retrievedPost.getTitle()).isEqualTo("Sample Post");
        verify(postRepository, times(1)).findById(eq(postId));
    }

    @Test
    @DisplayName("Delete a post by ID")
    void testDeletePost() {
        // Mock dependencies
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Call method
        postService.deletePost(postId);

        // Verify
        verify(postRepository, times(1)).deleteById(eq(postId));
    }

    @Test
    @DisplayName("Get a non-existing post should throw EntityNotFoundException")
    void testGetNonExistingPost() {
        // Mock dependencies
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Call method and expect exception
        assertThrows(EntityNotFoundException.class, () -> postService.getPost(postId));

        // Verify
        verify(postRepository, times(1)).findById(eq(postId));
    }
}
