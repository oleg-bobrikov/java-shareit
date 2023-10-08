package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Generator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    public void setUp() {
        commentMapper = Mappers.getMapper(CommentMapper.class);
    }

    @Test
    public void toDto_notNull_convertToDto() {
        // Arrange
        User author = Generator.makeUser1();
        LocalDateTime now = LocalDateTime.now();
        Comment comment = Comment.builder()
                .author(author)
                .text("Test comment")
                .id(1L)
                .created(now)
                .build();
        // Act
        CommentAnswerDto actual = commentMapper.toDto(comment);

        // Assert
        assertEquals(comment.getId(), actual.getId());
        assertEquals(comment.getText(), actual.getText());
        assertEquals(author.getName(), actual.getAuthorName());
        assertEquals(now, actual.getCreated());
    }

    @Test
    public void toDto_Null_convertToNull() {
        assertNull(commentMapper.toDto(null));
    }

    @Test
    public void testToDtoList() {
        // Arrange
        User author = Generator.makeUser1();
        LocalDateTime now = LocalDateTime.now();
        Comment comment1 = Comment.builder()
                .author(author)
                .text("Test comment")
                .id(1L)
                .created(now)
                .build();

        // Act
        List<CommentAnswerDto> commentDtoList = commentMapper.toDtoList(Collections.singletonList(comment1));

        // Assert
        assertThat(commentDtoList).asList().hasSize(1);
    }

    @Test
    public void toDtoList_Null_convertToNull() {
        assertNull(commentMapper.toDtoList(null));
    }

}
