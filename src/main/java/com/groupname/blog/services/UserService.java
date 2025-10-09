package com.groupname.blog.services;

import com.groupname.blog.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
}
