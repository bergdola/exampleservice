package de.unistuttgart.iste.gits.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Chapter.
 */
@Entity
@Table(name = "chapter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Chapter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "chapter_id", nullable = false, unique = true)
    private String chapterId;

    @NotNull
    @Column(name = "chapter_title", nullable = false)
    private String chapterTitle;

    @Column(name = "chapter_description")
    private String chapterDescription;

    @Column(name = "chaper_number")
    private Integer chaperNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "chapter")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "chapter" }, allowSetters = true)
    private Set<Content> contents = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "chapters" }, allowSetters = true)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Chapter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChapterId() {
        return this.chapterId;
    }

    public Chapter chapterId(String chapterId) {
        this.setChapterId(chapterId);
        return this;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterTitle() {
        return this.chapterTitle;
    }

    public Chapter chapterTitle(String chapterTitle) {
        this.setChapterTitle(chapterTitle);
        return this;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getChapterDescription() {
        return this.chapterDescription;
    }

    public Chapter chapterDescription(String chapterDescription) {
        this.setChapterDescription(chapterDescription);
        return this;
    }

    public void setChapterDescription(String chapterDescription) {
        this.chapterDescription = chapterDescription;
    }

    public Integer getChaperNumber() {
        return this.chaperNumber;
    }

    public Chapter chaperNumber(Integer chaperNumber) {
        this.setChaperNumber(chaperNumber);
        return this;
    }

    public void setChaperNumber(Integer chaperNumber) {
        this.chaperNumber = chaperNumber;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Chapter startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Chapter endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Content> getContents() {
        return this.contents;
    }

    public void setContents(Set<Content> contents) {
        if (this.contents != null) {
            this.contents.forEach(i -> i.setChapter(null));
        }
        if (contents != null) {
            contents.forEach(i -> i.setChapter(this));
        }
        this.contents = contents;
    }

    public Chapter contents(Set<Content> contents) {
        this.setContents(contents);
        return this;
    }

    public Chapter addContent(Content content) {
        this.contents.add(content);
        content.setChapter(this);
        return this;
    }

    public Chapter removeContent(Content content) {
        this.contents.remove(content);
        content.setChapter(null);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Chapter course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chapter)) {
            return false;
        }
        return id != null && id.equals(((Chapter) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Chapter{" +
            "id=" + getId() +
            ", chapterId='" + getChapterId() + "'" +
            ", chapterTitle='" + getChapterTitle() + "'" +
            ", chapterDescription='" + getChapterDescription() + "'" +
            ", chaperNumber=" + getChaperNumber() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            "}";
    }
}
