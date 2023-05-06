package de.unistuttgart.iste.gits.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Content.
 */
@Entity
@Table(name = "content")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Content implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "content_id", nullable = false, unique = true)
    private String contentId;

    @NotNull
    @Column(name = "content_name", nullable = false)
    private String contentName;

    @Column(name = "reward_points")
    private Integer rewardPoints;

    @Column(name = "worked_on")
    private Boolean workedOn;

    @ManyToOne
    @JsonIgnoreProperties(value = { "contents", "course" }, allowSetters = true)
    private Chapter chapter;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Content id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentId() {
        return this.contentId;
    }

    public Content contentId(String contentId) {
        this.setContentId(contentId);
        return this;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentName() {
        return this.contentName;
    }

    public Content contentName(String contentName) {
        this.setContentName(contentName);
        return this;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public Integer getRewardPoints() {
        return this.rewardPoints;
    }

    public Content rewardPoints(Integer rewardPoints) {
        this.setRewardPoints(rewardPoints);
        return this;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public Boolean getWorkedOn() {
        return this.workedOn;
    }

    public Content workedOn(Boolean workedOn) {
        this.setWorkedOn(workedOn);
        return this;
    }

    public void setWorkedOn(Boolean workedOn) {
        this.workedOn = workedOn;
    }

    public Chapter getChapter() {
        return this.chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Content chapter(Chapter chapter) {
        this.setChapter(chapter);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Content)) {
            return false;
        }
        return id != null && id.equals(((Content) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Content{" +
            "id=" + getId() +
            ", contentId='" + getContentId() + "'" +
            ", contentName='" + getContentName() + "'" +
            ", rewardPoints=" + getRewardPoints() +
            ", workedOn='" + getWorkedOn() + "'" +
            "}";
    }
}
