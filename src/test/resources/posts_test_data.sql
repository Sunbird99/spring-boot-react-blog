-- Insert test categories
INSERT INTO category (id, name) VALUES ('some-uuid-here', 'Tech');

-- Insert test tags
INSERT INTO tag (id, name) VALUES ('some-uuid-here', 'Java');

-- Insert test users
INSERT INTO "user" (id, username) VALUES ('some-author-uuid', 'johndoe');

-- Insert test posts
INSERT INTO post (id, title, status, category_id, author_id) VALUES
('post-uuid-1', 'First Post', 'PUBLISHED', 'some-uuid-here', 'some-author-uuid'),
('post-uuid-2', 'Second Post', 'DRAFT', 'some-uuid-here', 'some-author-uuid');