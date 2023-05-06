package de.unistuttgart.iste.gits.service;

import de.unistuttgart.iste.gits.domain.*; // for static metamodels
import de.unistuttgart.iste.gits.domain.Course;
import de.unistuttgart.iste.gits.repository.CourseRepository;
import de.unistuttgart.iste.gits.service.criteria.CourseCriteria;
import de.unistuttgart.iste.gits.service.dto.CourseDTO;
import de.unistuttgart.iste.gits.service.mapper.CourseMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Course} entities in the database.
 * The main input is a {@link CourseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CourseDTO} or a {@link Page} of {@link CourseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CourseQueryService extends QueryService<Course> {

    private final Logger log = LoggerFactory.getLogger(CourseQueryService.class);

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    public CourseQueryService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    /**
     * Return a {@link List} of {@link CourseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CourseDTO> findByCriteria(CourseCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Course> specification = createSpecification(criteria);
        return courseMapper.toDto(courseRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CourseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseDTO> findByCriteria(CourseCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Course> specification = createSpecification(criteria);
        return courseRepository.findAll(specification, page).map(courseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CourseCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Course> specification = createSpecification(criteria);
        return courseRepository.count(specification);
    }

    /**
     * Function to convert {@link CourseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Course> createSpecification(CourseCriteria criteria) {
        Specification<Course> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Course_.id));
            }
            if (criteria.getCourseId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCourseId(), Course_.courseId));
            }
            if (criteria.getCourseTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCourseTitle(), Course_.courseTitle));
            }
            if (criteria.getCourseDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCourseDescription(), Course_.courseDescription));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Course_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), Course_.endDate));
            }
            if (criteria.getPublishState() != null) {
                specification = specification.and(buildSpecification(criteria.getPublishState(), Course_.publishState));
            }
            if (criteria.getChapterId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getChapterId(), root -> root.join(Course_.chapters, JoinType.LEFT).get(Chapter_.id))
                    );
            }
        }
        return specification;
    }
}
