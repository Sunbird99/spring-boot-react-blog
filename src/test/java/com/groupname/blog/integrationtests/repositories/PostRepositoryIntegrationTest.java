package com.groupname.blog.integrationtests.repositories;

import com.groupname.blog.domain.PostStatus;
import com.groupname.blog.domain.entities.Category;
import com.groupname.blog.domain.entities.Post;
import com.groupname.blog.domain.entities.Tag;
import com.groupname.blog.domain.entities.User;
import com.groupname.blog.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Sets up an in-memory database and tests JPA repositories
class PostRepositoryIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    @Sql("/testdata/posts_test_data.sql") // Load initial test data
    void setupDatabase() {
        // This is optional when using @Sql but can be used for manual setup
    }

    @Test
    void findAllByStatus_ReturnsPostsWithGivenStatus() {
        // GIVEN
        PostStatus status = PostStatus.PUBLISHED;

        // WHEN
        List<Post> publishedPosts = postRepository.findAllByStatus(status);

        // THEN
        assertThat(publishedPosts).isNotNull();
        assertThat(publishedPosts).allMatch(post -> post.getStatus() == status);
    }

    @Test
    void findAllByStatusAndCategory_ReturnsPostsByStatusAndCategory() {
        // GIVEN
        PostStatus status = PostStatus.PUBLISHED;
        Category category = new Category();
        category.setName("Tech");
        category.setId(UUID.fromString("some-uuid-here"));

        // WHEN
        List<Post> posts = postRepository.findAllByStatusAndCategory(status, category);

        // THEN
        assertThat(posts).isNotNull();
        assertThat(posts).allMatch(post ->
                post.getStatus() == status &&
                        post.getCategory().getId().equals(category.getId())
        );
    }

    @Test
    void findAllByStatusAndTagsContaining_ReturnsPostsByStatusAndTag() {
        // GIVEN
        PostStatus status = PostStatus.DRAFT;
        Tag tag = new Tag();
        tag.setName("Java");
        tag.setId(UUID.fromString("some-uuid-here"));

        // WHEN
        List<Post> posts = postRepository.findAllByStatusAndTagsContaining(status, tag);

        // THEN
        assertThat(posts).isNotNull();
        assertThat(posts).allMatch(post ->
                post.getStatus() == status &&
                        post.getTags().contains(tag)
        );
    }

    @Test
    void findAllByAuthorAndStatus_ReturnsPostsByAuthorAndStatus() {
        // GIVEN
        User author = new User();
        author.setId(UUID.fromString("some-author-uuid"));
        author.setName("johndoe");
        PostStatus status = PostStatus.PUBLISHED;

        // WHEN
        List<Post> posts = postRepository.findAllByAuthorAndStatus(author, status);

        // THEN
        assertThat(posts).isNotNull();
        assertThat(posts).allMatch(post ->
                post.getAuthor().getId().equals(author.getId()) &&
                        post.getStatus() == status
        );
    }
}