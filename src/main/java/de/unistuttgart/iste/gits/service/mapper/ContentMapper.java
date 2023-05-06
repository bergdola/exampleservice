package de.unistuttgart.iste.gits.service.mapper;

import de.unistuttgart.iste.gits.domain.Chapter;
import de.unistuttgart.iste.gits.domain.Content;
import de.unistuttgart.iste.gits.service.dto.ChapterDTO;
import de.unistuttgart.iste.gits.service.dto.ContentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Content} and its DTO {@link ContentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContentMapper extends EntityMapper<ContentDTO, Content> {
    @Mapping(target = "chapter", source = "chapter", qualifiedByName = "chapterChapterTitle")
    ContentDTO toDto(Content s);

    @Named("chapterChapterTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "chapterTitle", source = "chapterTitle")
    ChapterDTO toDtoChapterChapterTitle(Chapter chapter);
}
