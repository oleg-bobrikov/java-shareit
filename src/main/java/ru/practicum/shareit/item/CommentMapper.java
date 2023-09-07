package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.converter.InstantConverter;
import ru.practicum.shareit.item.dto.CommentAnswerDto;

import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(imports = {Comment.class, User.class, InstantConverter.class}, componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    @Mapping(target = "created", expression = "java(InstantConverter.toPatternMillis(comment.getCreated().toInstant()))")
    CommentAnswerDto toDto(Comment comment);

    List<CommentAnswerDto> toDtoList(List<Comment> comments);

}
