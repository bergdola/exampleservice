package de.unistuttgart.iste.gits.service;

import de.unistuttgart.iste.gits.domain.*; // for static metamodels
import de.unistuttgart.iste.gits.domain.Content;
import de.unistuttgart.iste.gits.repository.ContentRepository;
import de.unistuttgart.iste.gits.service.criteria.ContentCriteria;
import de.unistuttgart.iste.gits.service.dto.ContentDTO;
import de.unistuttgart.iste.gits.service.mapper.ContentMapper;
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
 * Service for executing complex queries for {@link Content} entities in the database.
 * The main input is a {@link ContentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ContentDTO} or a {@link Page} of {@link ContentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ContentQueryService extends QueryService<Content> {

    private final Logger log = LoggerFactory.getLogger(ContentQueryService.class);

    private final ContentRepository contentRepository;

    private final ContentMapper contentMapper;

    public ContentQueryService(ContentRepository contentRepository, ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
    }

    /**
     * Return a {@link List} of {@link ContentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ContentDTO> findByCriteria(ContentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Content> specification = createSpecification(criteria);
        return contentMapper.toDto(contentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ContentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ContentDTO> findByCriteria(ContentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Content> specification = createSpecification(criteria);
        return contentRepository.findAll(specification, page).map(contentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ContentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Content> specification = createSpecification(criteria);
        return contentRepository.count(specification);
    }

    /**
     * Function to convert {@link ContentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Content> createSpecification(ContentCriteria criteria) {
        Specification<Content> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Content_.id));
            }
            if (criteria.getContentId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContentId(), Content_.contentId));
            }
            if (criteria.getContentName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContentName(), Content_.contentName));
            }
            if (criteria.getRewardPoints() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRewardPoints(), Content_.rewardPoints));
            }
            if (criteria.getWorkedOn() != null) {
                specification = specification.and(buildSpecification(criteria.getWorkedOn(), Content_.workedOn));
            }
            if (criteria.getChapterId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getChapterId(), root -> root.join(Content_.chapter, JoinType.LEFT).get(Chapter_.id))
                    );
            }
        }
        return specification;
    }
}
