package com.groupname.blog.repositories;

import com.groupname.blog.domain.PostStatus;
import com.groupname.blog.domain.entities.Category;
import com.groupname.blog.domain.entities.Post;
import com.groupname.blog.domain.entities.Tag;
import com.groupname.blog.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("PostRepository Unit Tests")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    private Post post1;
    private Post post2;
    private Category category;
    private Tag tag;
    private User author;

    @BeforeEach
    void setUp() {
        // Set up your entities
        tag = new Tag();
        tag.setId(UUID.randomUUID());
        tag.setName("Tech");

        category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Technology");

        author = new User();
        author.setId(UUID.randomUUID());
        author.setName("john_doe");

        post1 = new Post();
        post1.setId(UUID.randomUUID());
        post1.setTitle("Post 1");
        post1.setStatus(PostStatus.PUBLISHED);
        post1.setCategory(category);
        post1.setTags(Set.of(tag));
        post1.setAuthor(author);

        post2 = new Post();
        post2.setId(UUID.randomUUID());
        post2.setTitle("Post 2");
        post2.setStatus(PostStatus.DRAFT);
        post2.setCategory(category);
        post2.setTags(Set.of(tag));
        post2.setAuthor(author);

        postRepository.save(post1);
        postRepository.save(post2);
    }

    @Test
    @DisplayName("Find all posts by status and category and tags containing")
    void testFindAllByStatusAndCategoryAndTagsContaining() {
        List<Post> posts = postRepository.findAllByStatusAndCategoryAndTagsContaining(PostStatus.PUBLISHED, category, tag);

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Post 1");
    }

    @Test
    @DisplayName("Find all posts by status and category")
    void testFindAllByStatusAndCategory() {
        List<Post> posts = postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, category);

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Post 1");
    }

    @Test
    @DisplayName("Find all posts by status and tags containing")
    void testFindAllByStatusAndTagsContaining() {
        List<Post> posts = postRepository.findAllByStatusAndTagsContaining(PostStatus.DRAFT, tag);

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Post 2");
    }

    @Test
    @DisplayName("Find all posts by status")
    void testFindAllByStatus() {
        List<Post> posts = postRepository.findAllByStatus(PostStatus.PUBLISHED);

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Post 1");
    }

    @Test
    @DisplayName("Find all posts by author and status")
    void testFindAllByAuthorAndStatus() {
        List<Post> posts = postRepository.findAllByAuthorAndStatus(author, PostStatus.DRAFT);

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Post 2");
    }
}