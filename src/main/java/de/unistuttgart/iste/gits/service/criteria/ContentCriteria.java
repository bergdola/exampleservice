package de.unistuttgart.iste.gits.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link de.unistuttgart.iste.gits.domain.Content} entity. This class is used
 * in {@link de.unistuttgart.iste.gits.web.rest.ContentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /contents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter contentId;

    private StringFilter contentName;

    private IntegerFilter rewardPoints;

    private BooleanFilter workedOn;

    private LongFilter chapterId;

    private Boolean distinct;

    public ContentCriteria() {}

    public ContentCriteria(ContentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.contentId = other.contentId == null ? null : other.contentId.copy();
        this.contentName = other.contentName == null ? null : other.contentName.copy();
        this.rewardPoints = other.rewardPoints == null ? null : other.rewardPoints.copy();
        this.workedOn = other.workedOn == null ? null : other.workedOn.copy();
        this.chapterId = other.chapterId == null ? null : other.chapterId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ContentCriteria copy() {
        return new ContentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getContentId() {
        return contentId;
    }

    public StringFilter contentId() {
        if (contentId == null) {
            contentId = new StringFilter();
        }
        return contentId;
    }

    public void setContentId(StringFilter contentId) {
        this.contentId = contentId;
    }

    public StringFilter getContentName() {
        return contentName;
    }

    public StringFilter contentName() {
        if (contentName == null) {
            contentName = new StringFilter();
        }
        return contentName;
    }

    public void setContentName(StringFilter contentName) {
        this.contentName = contentName;
    }

    public IntegerFilter getRewardPoints() {
        return rewardPoints;
    }

    public IntegerFilter rewardPoints() {
        if (rewardPoints == null) {
            rewardPoints = new IntegerFilter();
        }
        return rewardPoints;
    }

    public void setRewardPoints(IntegerFilter rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public BooleanFilter getWorkedOn() {
        return workedOn;
    }

    public BooleanFilter workedOn() {
        if (workedOn == null) {
            workedOn = new BooleanFilter();
        }
        return workedOn;
    }

    public void setWorkedOn(BooleanFilter workedOn) {
        this.workedOn = workedOn;
    }

    public LongFilter getChapterId() {
        return chapterId;
    }

    public LongFilter chapterId() {
        if (chapterId == null) {
            chapterId = new LongFilter();
        }
        return chapterId;
    }

    public void setChapterId(LongFilter chapterId) {
        this.chapterId = chapterId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ContentCriteria that = (ContentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(contentId, that.contentId) &&
            Objects.equals(contentName, that.contentName) &&
            Objects.equals(rewardPoints, that.rewardPoints) &&
            Objects.equals(workedOn, that.workedOn) &&
            Objects.equals(chapterId, that.chapterId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contentId, contentName, rewardPoints, workedOn, chapterId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContentCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (contentId != null ? "contentId=" + contentId + ", " : "") +
            (contentName != null ? "contentName=" + contentName + ", " : "") +
            (rewardPoints != null ? "rewardPoints=" + rewardPoints + ", " : "") +
            (workedOn != null ? "workedOn=" + workedOn + ", " : "") +
            (chapterId != null ? "chapterId=" + chapterId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
