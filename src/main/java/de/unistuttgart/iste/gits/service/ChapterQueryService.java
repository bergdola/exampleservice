package de.unistuttgart.iste.gits.service;

import de.unistuttgart.iste.gits.domain.*; // for static metamodels
import de.unistuttgart.iste.gits.domain.Chapter;
import de.unistuttgart.iste.gits.repository.ChapterRepository;
import de.unistuttgart.iste.gits.service.criteria.ChapterCriteria;
import de.unistuttgart.iste.gits.service.dto.ChapterDTO;
import de.unistuttgart.iste.gits.service.mapper.ChapterMapper;
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
 * Service for executing complex queries for {@link Chapter} entities in the database.
 * The main input is a {@link ChapterCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ChapterDTO} or a {@link Page} of {@link ChapterDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChapterQueryService extends QueryService<Chapter> {

    private final Logger log = LoggerFactory.getLogger(ChapterQueryService.class);

    private final ChapterRepository chapterRepository;

    private final ChapterMapper chapterMapper;

    public ChapterQueryService(ChapterRepository chapterRepository, ChapterMapper chapterMapper) {
        this.chapterRepository = chapterRepository;
        this.chapterMapper = chapterMapper;
    }

    /**
     * Return a {@link List} of {@link ChapterDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ChapterDTO> findByCriteria(ChapterCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Chapter> specification = createSpecification(criteria);
        return chapterMapper.toDto(chapterRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ChapterDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ChapterDTO> findByCriteria(ChapterCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Chapter> specification = createSpecification(criteria);
        return chapterRepository.findAll(specification, page).map(chapterMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChapterCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Chapter> specification = createSpecification(criteria);
        return chapterRepository.count(specification);
    }

    /**
     * Function to convert {@link ChapterCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Chapter> createSpecification(ChapterCriteria criteria) {
        Specification<Chapter> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Chapter_.id));
            }
            if (criteria.getChapterId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getChapterId(), Chapter_.chapterId));
            }
            if (criteria.getChapterTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getChapterTitle(), Chapter_.chapterTitle));
            }
            if (criteria.getChapterDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getChapterDescription(), Chapter_.chapterDescription));
            }
            if (criteria.getChaperNumber() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getChaperNumber(), Chapter_.chaperNumber));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Chapter_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), Chapter_.endDate));
            }
            if (criteria.getContentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getContentId(), root -> root.join(Chapter_.contents, JoinType.LEFT).get(Content_.id))
                    );
            }
            if (criteria.getCourseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCourseId(), root -> root.join(Chapter_.course, JoinType.LEFT).get(Course_.id))
                    );
            }
        }
        return specification;
    }
}
