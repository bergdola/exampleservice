package de.unistuttgart.iste.gits.repository;

import de.unistuttgart.iste.gits.domain.Chapter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Chapter entity.
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>, JpaSpecificationExecutor<Chapter> {
    default Optional<Chapter> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Chapter> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Chapter> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct chapter from Chapter chapter left join fetch chapter.course",
        countQuery = "select count(distinct chapter) from Chapter chapter"
    )
    Page<Chapter> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct chapter from Chapter chapter left join fetch chapter.course")
    List<Chapter> findAllWithToOneRelationships();

    @Query("select chapter from Chapter chapter left join fetch chapter.course where chapter.id =:id")
    Optional<Chapter> findOneWithToOneRelationships(@Param("id") Long id);
}
