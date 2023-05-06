package de.unistuttgart.iste.gits.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link de.unistuttgart.iste.gits.domain.Chapter} entity. This class is used
 * in {@link de.unistuttgart.iste.gits.web.rest.ChapterResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /chapters?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter chapterId;

    private StringFilter chapterTitle;

    private StringFilter chapterDescription;

    private IntegerFilter chaperNumber;

    private LocalDateFilter startDate;

    private LocalDateFilter endDate;

    private LongFilter contentId;

    private LongFilter courseId;

    private Boolean distinct;

    public ChapterCriteria() {}

    public ChapterCriteria(ChapterCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.chapterId = other.chapterId == null ? null : other.chapterId.copy();
        this.chapterTitle = other.chapterTitle == null ? null : other.chapterTitle.copy();
        this.chapterDescription = other.chapterDescription == null ? null : other.chapterDescription.copy();
        this.chaperNumber = other.chaperNumber == null ? null : other.chaperNumber.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.contentId = other.contentId == null ? null : other.contentId.copy();
        this.courseId = other.courseId == null ? null : other.courseId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ChapterCriteria copy() {
        return new ChapterCriteria(this);
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

    public StringFilter getChapterId() {
        return chapterId;
    }

    public StringFilter chapterId() {
        if (chapterId == null) {
            chapterId = new StringFilter();
        }
        return chapterId;
    }

    public void setChapterId(StringFilter chapterId) {
        this.chapterId = chapterId;
    }

    public StringFilter getChapterTitle() {
        return chapterTitle;
    }

    public StringFilter chapterTitle() {
        if (chapterTitle == null) {
            chapterTitle = new StringFilter();
        }
        return chapterTitle;
    }

    public void setChapterTitle(StringFilter chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public StringFilter getChapterDescription() {
        return chapterDescription;
    }

    public StringFilter chapterDescription() {
        if (chapterDescription == null) {
            chapterDescription = new StringFilter();
        }
        return chapterDescription;
    }

    public void setChapterDescription(StringFilter chapterDescription) {
        this.chapterDescription = chapterDescription;
    }

    public IntegerFilter getChaperNumber() {
        return chaperNumber;
    }

    public IntegerFilter chaperNumber() {
        if (chaperNumber == null) {
            chaperNumber = new IntegerFilter();
        }
        return chaperNumber;
    }

    public void setChaperNumber(IntegerFilter chaperNumber) {
        this.chaperNumber = chaperNumber;
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

    public LongFilter getContentId() {
        return contentId;
    }

    public LongFilter contentId() {
        if (contentId == null) {
            contentId = new LongFilter();
        }
        return contentId;
    }

    public void setContentId(LongFilter contentId) {
        this.contentId = contentId;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public LongFilter courseId() {
        if (courseId == null) {
            courseId = new LongFilter();
        }
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
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
        final ChapterCriteria that = (ChapterCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(chapterId, that.chapterId) &&
            Objects.equals(chapterTitle, that.chapterTitle) &&
            Objects.equals(chapterDescription, that.chapterDescription) &&
            Objects.equals(chaperNumber, that.chaperNumber) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(contentId, that.contentId) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            chapterId,
            chapterTitle,
            chapterDescription,
            chaperNumber,
            startDate,
            endDate,
            contentId,
            courseId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (chapterId != null ? "chapterId=" + chapterId + ", " : "") +
            (chapterTitle != null ? "chapterTitle=" + chapterTitle + ", " : "") +
            (chapterDescription != null ? "chapterDescription=" + chapterDescription + ", " : "") +
            (chaperNumber != null ? "chaperNumber=" + chaperNumber + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (contentId != null ? "contentId=" + contentId + ", " : "") +
            (courseId != null ? "courseId=" + courseId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
