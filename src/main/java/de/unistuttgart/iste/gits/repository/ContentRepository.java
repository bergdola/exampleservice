package de.unistuttgart.iste.gits.repository;

import de.unistuttgart.iste.gits.domain.Content;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Content entity.
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {
    default Optional<Content> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Content> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Content> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct content from Content content left join fetch content.chapter",
        countQuery = "select count(distinct content) from Content content"
    )
    Page<Content> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct content from Content content left join fetch content.chapter")
    List<Content> findAllWithToOneRelationships();

    @Query("select content from Content content left join fetch content.chapter where content.id =:id")
    Optional<Content> findOneWithToOneRelationships(@Param("id") Long id);
}
