package com.groupname.blog.mappers;

import com.groupname.blog.domain.CreatePostRequest;
import com.groupname.blog.domain.UpdatePostRequest;
import com.groupname.blog.domain.dtos.CreatePostRequestDto;
import com.groupname.blog.domain.dtos.PostDto;
import com.groupname.blog.domain.dtos.UpdatePostRequestDto;
import com.groupname.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "status", source = "status")

    PostDto toDto(Post post);
    CreatePostRequest toCreatePostRequest(CreatePostRequestDto dto);
    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto dto);
}
