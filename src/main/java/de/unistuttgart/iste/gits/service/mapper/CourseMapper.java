package de.unistuttgart.iste.gits.service.mapper;

import de.unistuttgart.iste.gits.domain.Course;
import de.unistuttgart.iste.gits.service.dto.CourseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {}
