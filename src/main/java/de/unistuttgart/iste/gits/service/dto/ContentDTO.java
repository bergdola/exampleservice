package de.unistuttgart.iste.gits.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link de.unistuttgart.iste.gits.domain.Content} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContentDTO implements Serializable {

    private Long id;

    @NotNull
    private String contentId;

    @NotNull
    private String contentName;

    private Integer rewardPoints;

    private Boolean workedOn;

    private ChapterDTO chapter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public Boolean getWorkedOn() {
        return workedOn;
    }

    public void setWorkedOn(Boolean workedOn) {
        this.workedOn = workedOn;
    }

    public ChapterDTO getChapter() {
        return chapter;
    }

    public void setChapter(ChapterDTO chapter) {
        this.chapter = chapter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentDTO)) {
            return false;
        }

        ContentDTO contentDTO = (ContentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, contentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContentDTO{" +
            "id=" + getId() +
            ", contentId='" + getContentId() + "'" +
            ", contentName='" + getContentName() + "'" +
            ", rewardPoints=" + getRewardPoints() +
            ", workedOn='" + getWorkedOn() + "'" +
            ", chapter=" + getChapter() +
            "}";
    }
}
