package de.unistuttgart.iste.gits.service.mapper;

import de.unistuttgart.iste.gits.domain.Chapter;
import de.unistuttgart.iste.gits.domain.Course;
import de.unistuttgart.iste.gits.service.dto.ChapterDTO;
import de.unistuttgart.iste.gits.service.dto.CourseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Chapter} and its DTO {@link ChapterDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChapterMapper extends EntityMapper<ChapterDTO, Chapter> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseCourseTitle")
    ChapterDTO toDto(Chapter s);

    @Named("courseCourseTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "courseTitle", source = "courseTitle")
    CourseDTO toDtoCourseCourseTitle(Course course);
}
