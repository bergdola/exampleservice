package de.unistuttgart.iste.gits.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.unistuttgart.iste.gits.IntegrationTest;
import de.unistuttgart.iste.gits.domain.Chapter;
import de.unistuttgart.iste.gits.domain.Course;
import de.unistuttgart.iste.gits.repository.CourseRepository;
import de.unistuttgart.iste.gits.service.criteria.CourseCriteria;
import de.unistuttgart.iste.gits.service.dto.CourseDTO;
import de.unistuttgart.iste.gits.service.mapper.CourseMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CourseResourceIT {

    private static final String DEFAULT_COURSE_ID = "AAAAAAAAAA";
    private static final String UPDATED_COURSE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_COURSE_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_COURSE_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_COURSE_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_COURSE_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_PUBLISH_STATE = false;
    private static final Boolean UPDATED_PUBLISH_STATE = true;

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMockMvc;

    private Course course;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createEntity(EntityManager em) {
        Course course = new Course()
            .courseId(DEFAULT_COURSE_ID)
            .courseTitle(DEFAULT_COURSE_TITLE)
            .courseDescription(DEFAULT_COURSE_DESCRIPTION)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .publishState(DEFAULT_PUBLISH_STATE);
        return course;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createUpdatedEntity(EntityManager em) {
        Course course = new Course()
            .courseId(UPDATED_COURSE_ID)
            .courseTitle(UPDATED_COURSE_TITLE)
            .courseDescription(UPDATED_COURSE_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .publishState(UPDATED_PUBLISH_STATE);
        return course;
    }

    @BeforeEach
    public void initTest() {
        course = createEntity(em);
    }

    @Test
    @Transactional
    void createCourse() throws Exception {
        int databaseSizeBeforeCreate = courseRepository.findAll().size();
        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isCreated());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate + 1);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCourseId()).isEqualTo(DEFAULT_COURSE_ID);
        assertThat(testCourse.getCourseTitle()).isEqualTo(DEFAULT_COURSE_TITLE);
        assertThat(testCourse.getCourseDescription()).isEqualTo(DEFAULT_COURSE_DESCRIPTION);
        assertThat(testCourse.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testCourse.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testCourse.getPublishState()).isEqualTo(DEFAULT_PUBLISH_STATE);
    }

    @Test
    @Transactional
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);
        CourseDTO courseDTO = courseMapper.toDto(course);

        int databaseSizeBeforeCreate = courseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCourseIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = courseRepository.findAll().size();
        // set the field null
        course.setCourseId(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCourseTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = courseRepository.findAll().size();
        // set the field null
        course.setCourseTitle(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourses() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].courseId").value(hasItem(DEFAULT_COURSE_ID)))
            .andExpect(jsonPath("$.[*].courseTitle").value(hasItem(DEFAULT_COURSE_TITLE)))
            .andExpect(jsonPath("$.[*].courseDescription").value(hasItem(DEFAULT_COURSE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].publishState").value(hasItem(DEFAULT_PUBLISH_STATE.booleanValue())));
    }

    @Test
    @Transactional
    void getCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get the course
        restCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(course.getId().intValue()))
            .andExpect(jsonPath("$.courseId").value(DEFAULT_COURSE_ID))
            .andExpect(jsonPath("$.courseTitle").value(DEFAULT_COURSE_TITLE))
            .andExpect(jsonPath("$.courseDescription").value(DEFAULT_COURSE_DESCRIPTION))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.publishState").value(DEFAULT_PUBLISH_STATE.booleanValue()));
    }

    @Test
    @Transactional
    void getCoursesByIdFiltering() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        Long id = course.getId();

        defaultCourseShouldBeFound("id.equals=" + id);
        defaultCourseShouldNotBeFound("id.notEquals=" + id);

        defaultCourseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCourseShouldNotBeFound("id.greaterThan=" + id);

        defaultCourseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCourseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseIdIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseId equals to DEFAULT_COURSE_ID
        defaultCourseShouldBeFound("courseId.equals=" + DEFAULT_COURSE_ID);

        // Get all the courseList where courseId equals to UPDATED_COURSE_ID
        defaultCourseShouldNotBeFound("courseId.equals=" + UPDATED_COURSE_ID);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseIdIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseId in DEFAULT_COURSE_ID or UPDATED_COURSE_ID
        defaultCourseShouldBeFound("courseId.in=" + DEFAULT_COURSE_ID + "," + UPDATED_COURSE_ID);

        // Get all the courseList where courseId equals to UPDATED_COURSE_ID
        defaultCourseShouldNotBeFound("courseId.in=" + UPDATED_COURSE_ID);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseId is not null
        defaultCourseShouldBeFound("courseId.specified=true");

        // Get all the courseList where courseId is null
        defaultCourseShouldNotBeFound("courseId.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByCourseIdContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseId contains DEFAULT_COURSE_ID
        defaultCourseShouldBeFound("courseId.contains=" + DEFAULT_COURSE_ID);

        // Get all the courseList where courseId contains UPDATED_COURSE_ID
        defaultCourseShouldNotBeFound("courseId.contains=" + UPDATED_COURSE_ID);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseIdNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseId does not contain DEFAULT_COURSE_ID
        defaultCourseShouldNotBeFound("courseId.doesNotContain=" + DEFAULT_COURSE_ID);

        // Get all the courseList where courseId does not contain UPDATED_COURSE_ID
        defaultCourseShouldBeFound("courseId.doesNotContain=" + UPDATED_COURSE_ID);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseTitle equals to DEFAULT_COURSE_TITLE
        defaultCourseShouldBeFound("courseTitle.equals=" + DEFAULT_COURSE_TITLE);

        // Get all the courseList where courseTitle equals to UPDATED_COURSE_TITLE
        defaultCourseShouldNotBeFound("courseTitle.equals=" + UPDATED_COURSE_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTitleIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseTitle in DEFAULT_COURSE_TITLE or UPDATED_COURSE_TITLE
        defaultCourseShouldBeFound("courseTitle.in=" + DEFAULT_COURSE_TITLE + "," + UPDATED_COURSE_TITLE);

        // Get all the courseList where courseTitle equals to UPDATED_COURSE_TITLE
        defaultCourseShouldNotBeFound("courseTitle.in=" + UPDATED_COURSE_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseTitle is not null
        defaultCourseShouldBeFound("courseTitle.specified=true");

        // Get all the courseList where courseTitle is null
        defaultCourseShouldNotBeFound("courseTitle.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTitleContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseTitle contains DEFAULT_COURSE_TITLE
        defaultCourseShouldBeFound("courseTitle.contains=" + DEFAULT_COURSE_TITLE);

        // Get all the courseList where courseTitle contains UPDATED_COURSE_TITLE
        defaultCourseShouldNotBeFound("courseTitle.contains=" + UPDATED_COURSE_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseTitleNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseTitle does not contain DEFAULT_COURSE_TITLE
        defaultCourseShouldNotBeFound("courseTitle.doesNotContain=" + DEFAULT_COURSE_TITLE);

        // Get all the courseList where courseTitle does not contain UPDATED_COURSE_TITLE
        defaultCourseShouldBeFound("courseTitle.doesNotContain=" + UPDATED_COURSE_TITLE);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseDescription equals to DEFAULT_COURSE_DESCRIPTION
        defaultCourseShouldBeFound("courseDescription.equals=" + DEFAULT_COURSE_DESCRIPTION);

        // Get all the courseList where courseDescription equals to UPDATED_COURSE_DESCRIPTION
        defaultCourseShouldNotBeFound("courseDescription.equals=" + UPDATED_COURSE_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseDescription in DEFAULT_COURSE_DESCRIPTION or UPDATED_COURSE_DESCRIPTION
        defaultCourseShouldBeFound("courseDescription.in=" + DEFAULT_COURSE_DESCRIPTION + "," + UPDATED_COURSE_DESCRIPTION);

        // Get all the courseList where courseDescription equals to UPDATED_COURSE_DESCRIPTION
        defaultCourseShouldNotBeFound("courseDescription.in=" + UPDATED_COURSE_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseDescription is not null
        defaultCourseShouldBeFound("courseDescription.specified=true");

        // Get all the courseList where courseDescription is null
        defaultCourseShouldNotBeFound("courseDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByCourseDescriptionContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseDescription contains DEFAULT_COURSE_DESCRIPTION
        defaultCourseShouldBeFound("courseDescription.contains=" + DEFAULT_COURSE_DESCRIPTION);

        // Get all the courseList where courseDescription contains UPDATED_COURSE_DESCRIPTION
        defaultCourseShouldNotBeFound("courseDescription.contains=" + UPDATED_COURSE_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCoursesByCourseDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where courseDescription does not contain DEFAULT_COURSE_DESCRIPTION
        defaultCourseShouldNotBeFound("courseDescription.doesNotContain=" + DEFAULT_COURSE_DESCRIPTION);

        // Get all the courseList where courseDescription does not contain UPDATED_COURSE_DESCRIPTION
        defaultCourseShouldBeFound("courseDescription.doesNotContain=" + UPDATED_COURSE_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDate equals to DEFAULT_START_DATE
        defaultCourseShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the courseList where startDate equals to UPDATED_START_DATE
        defaultCourseShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultCourseShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the courseList where startDate equals to UPDATED_START_DATE
        defaultCourseShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDate is not null
        defaultCourseShouldBeFound("startDate.specified=true");

        // Get all the courseList where startDate is null
        defaultCourseShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultCourseShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the courseList where startDate is greater than or equal to UPDATED_START_DATE
        defaultCourseShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDate is less than or equal to DEFAULT_START_DATE
        defaultCourseShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the courseList where startDate is less than or equal to SMALLER_START_DATE
        defaultCourseShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDate is less than DEFAULT_START_DATE
        defaultCourseShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the courseList where startDate is less than UPDATED_START_DATE
        defaultCourseShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startDate is greater than DEFAULT_START_DATE
        defaultCourseShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the courseList where startDate is greater than SMALLER_START_DATE
        defaultCourseShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDate equals to DEFAULT_END_DATE
        defaultCourseShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the courseList where endDate equals to UPDATED_END_DATE
        defaultCourseShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultCourseShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the courseList where endDate equals to UPDATED_END_DATE
        defaultCourseShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDate is not null
        defaultCourseShouldBeFound("endDate.specified=true");

        // Get all the courseList where endDate is null
        defaultCourseShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDate is greater than or equal to DEFAULT_END_DATE
        defaultCourseShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the courseList where endDate is greater than or equal to UPDATED_END_DATE
        defaultCourseShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDate is less than or equal to DEFAULT_END_DATE
        defaultCourseShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the courseList where endDate is less than or equal to SMALLER_END_DATE
        defaultCourseShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDate is less than DEFAULT_END_DATE
        defaultCourseShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the courseList where endDate is less than UPDATED_END_DATE
        defaultCourseShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endDate is greater than DEFAULT_END_DATE
        defaultCourseShouldNotBeFound("endDate.greaterThan=" + DEFAULT_END_DATE);

        // Get all the courseList where endDate is greater than SMALLER_END_DATE
        defaultCourseShouldBeFound("endDate.greaterThan=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllCoursesByPublishStateIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where publishState equals to DEFAULT_PUBLISH_STATE
        defaultCourseShouldBeFound("publishState.equals=" + DEFAULT_PUBLISH_STATE);

        // Get all the courseList where publishState equals to UPDATED_PUBLISH_STATE
        defaultCourseShouldNotBeFound("publishState.equals=" + UPDATED_PUBLISH_STATE);
    }

    @Test
    @Transactional
    void getAllCoursesByPublishStateIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where publishState in DEFAULT_PUBLISH_STATE or UPDATED_PUBLISH_STATE
        defaultCourseShouldBeFound("publishState.in=" + DEFAULT_PUBLISH_STATE + "," + UPDATED_PUBLISH_STATE);

        // Get all the courseList where publishState equals to UPDATED_PUBLISH_STATE
        defaultCourseShouldNotBeFound("publishState.in=" + UPDATED_PUBLISH_STATE);
    }

    @Test
    @Transactional
    void getAllCoursesByPublishStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where publishState is not null
        defaultCourseShouldBeFound("publishState.specified=true");

        // Get all the courseList where publishState is null
        defaultCourseShouldNotBeFound("publishState.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByChapterIsEqualToSomething() throws Exception {
        Chapter chapter;
        if (TestUtil.findAll(em, Chapter.class).isEmpty()) {
            courseRepository.saveAndFlush(course);
            chapter = ChapterResourceIT.createEntity(em);
        } else {
            chapter = TestUtil.findAll(em, Chapter.class).get(0);
        }
        em.persist(chapter);
        em.flush();
        course.addChapter(chapter);
        courseRepository.saveAndFlush(course);
        Long chapterId = chapter.getId();

        // Get all the courseList where chapter equals to chapterId
        defaultCourseShouldBeFound("chapterId.equals=" + chapterId);

        // Get all the courseList where chapter equals to (chapterId + 1)
        defaultCourseShouldNotBeFound("chapterId.equals=" + (chapterId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseShouldBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].courseId").value(hasItem(DEFAULT_COURSE_ID)))
            .andExpect(jsonPath("$.[*].courseTitle").value(hasItem(DEFAULT_COURSE_TITLE)))
            .andExpect(jsonPath("$.[*].courseDescription").value(hasItem(DEFAULT_COURSE_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].publishState").value(hasItem(DEFAULT_PUBLISH_STATE.booleanValue())));

        // Check, that the count call also returns 1
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseShouldNotBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourse() throws Exception {
        // Get the course
        restCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).get();
        // Disconnect from session so that the updates on updatedCourse are not directly saved in db
        em.detach(updatedCourse);
        updatedCourse
            .courseId(UPDATED_COURSE_ID)
            .courseTitle(UPDATED_COURSE_TITLE)
            .courseDescription(UPDATED_COURSE_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .publishState(UPDATED_PUBLISH_STATE);
        CourseDTO courseDTO = courseMapper.toDto(updatedCourse);

        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCourseId()).isEqualTo(UPDATED_COURSE_ID);
        assertThat(testCourse.getCourseTitle()).isEqualTo(UPDATED_COURSE_TITLE);
        assertThat(testCourse.getCourseDescription()).isEqualTo(UPDATED_COURSE_DESCRIPTION);
        assertThat(testCourse.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testCourse.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testCourse.getPublishState()).isEqualTo(UPDATED_PUBLISH_STATE);
    }

    @Test
    @Transactional
    void putNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse.courseDescription(UPDATED_COURSE_DESCRIPTION).endDate(UPDATED_END_DATE).publishState(UPDATED_PUBLISH_STATE);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCourseId()).isEqualTo(DEFAULT_COURSE_ID);
        assertThat(testCourse.getCourseTitle()).isEqualTo(DEFAULT_COURSE_TITLE);
        assertThat(testCourse.getCourseDescription()).isEqualTo(UPDATED_COURSE_DESCRIPTION);
        assertThat(testCourse.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testCourse.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testCourse.getPublishState()).isEqualTo(UPDATED_PUBLISH_STATE);
    }

    @Test
    @Transactional
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse
            .courseId(UPDATED_COURSE_ID)
            .courseTitle(UPDATED_COURSE_TITLE)
            .courseDescription(UPDATED_COURSE_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .publishState(UPDATED_PUBLISH_STATE);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCourseId()).isEqualTo(UPDATED_COURSE_ID);
        assertThat(testCourse.getCourseTitle()).isEqualTo(UPDATED_COURSE_TITLE);
        assertThat(testCourse.getCourseDescription()).isEqualTo(UPDATED_COURSE_DESCRIPTION);
        assertThat(testCourse.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testCourse.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testCourse.getPublishState()).isEqualTo(UPDATED_PUBLISH_STATE);
    }

    @Test
    @Transactional
    void patchNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeDelete = courseRepository.findAll().size();

        // Delete the course
        restCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, course.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
