package de.unistuttgart.iste.gits.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link de.unistuttgart.iste.gits.domain.Course} entity. This class is used
 * in {@link de.unistuttgart.iste.gits.web.rest.CourseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /courses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter courseId;

    private StringFilter courseTitle;

    private StringFilter courseDescription;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private BooleanFilter publishState;

    private LongFilter chapterId;

    private Boolean distinct;

    public CourseCriteria() {}

    public CourseCriteria(CourseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.courseId = other.courseId == null ? null : other.courseId.copy();
        this.courseTitle = other.courseTitle == null ? null : other.courseTitle.copy();
        this.courseDescription = other.courseDescription == null ? null : other.courseDescription.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.publishState = other.publishState == null ? null : other.publishState.copy();
        this.chapterId = other.chapterId == null ? null : other.chapterId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CourseCriteria copy() {
        return new CourseCriteria(this);
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

    public StringFilter getCourseId() {
        return courseId;
    }

    public StringFilter courseId() {
        if (courseId == null) {
            courseId = new StringFilter();
        }
        return courseId;
    }

    public void setCourseId(StringFilter courseId) {
        this.courseId = courseId;
    }

    public StringFilter getCourseTitle() {
        return courseTitle;
    }

    public StringFilter courseTitle() {
        if (courseTitle == null) {
            courseTitle = new StringFilter();
        }
        return courseTitle;
    }

    public void setCourseTitle(StringFilter courseTitle) {
        this.courseTitle = courseTitle;
    }

    public StringFilter getCourseDescription() {
        return courseDescription;
    }

    public StringFilter courseDescription() {
        if (courseDescription == null) {
            courseDescription = new StringFilter();
        }
        return courseDescription;
    }

    public void setCourseDescription(StringFilter courseDescription) {
        this.courseDescription = courseDescription;
    }

    public LocalDateFilter getStartDate() {
        return startDate;
    }

    public LocalDateFilter startDate() {
        if (startDate == null) {
            startDate = new LocalDateFilter();
        }
        return startDate;
    }

    public void setStartDate(LocalDateFilter startDate) {
        this.startDate = startDate;
    }

    public LocalDateFilter getEndDate() {
        return endDate;
    }

    public LocalDateFilter endDate() {
        if (endDate == null) {
            endDate = new LocalDateFilter();
        }
        return endDate;
    }

    public void setEndDate(LocalDateFilter endDate) {
        this.endDate = endDate;
    }

    public BooleanFilter getPublishState() {
        return publishState;
    }

    public BooleanFilter publishState() {
        if (publishState == null) {
            publishState = new BooleanFilter();
        }
        return publishState;
    }

    public void setPublishState(BooleanFilter publishState) {
        this.publishState = publishState;
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
        final CourseCriteria that = (CourseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(courseTitle, that.courseTitle) &&
            Objects.equals(courseDescription, that.courseDescription) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(publishState, that.publishState) &&
            Objects.equals(chapterId, that.chapterId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, courseTitle, courseDescription, startDate, endDate, publishState, chapterId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (courseId != null ? "courseId=" + courseId + ", " : "") +
            (courseTitle != null ? "courseTitle=" + courseTitle + ", " : "") +
            (courseDescription != null ? "courseDescription=" + courseDescription + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (publishState != null ? "publishState=" + publishState + ", " : "") +
            (chapterId != null ? "chapterId=" + chapterId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
