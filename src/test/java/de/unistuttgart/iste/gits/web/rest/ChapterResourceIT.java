package de.unistuttgart.iste.gits.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.unistuttgart.iste.gits.IntegrationTest;
import de.unistuttgart.iste.gits.domain.Chapter;
import de.unistuttgart.iste.gits.domain.Content;
import de.unistuttgart.iste.gits.domain.Course;
import de.unistuttgart.iste.gits.repository.ChapterRepository;
import de.unistuttgart.iste.gits.service.ChapterService;
import de.unistuttgart.iste.gits.service.criteria.ChapterCriteria;
import de.unistuttgart.iste.gits.service.dto.ChapterDTO;
import de.unistuttgart.iste.gits.service.mapper.ChapterMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChapterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChapterResourceIT {

    private static final String DEFAULT_CHAPTER_ID = "AAAAAAAAAA";
    private static final String UPDATED_CHAPTER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CHAPTER_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_CHAPTER_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CHAPTER_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_CHAPTER_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CHAPER_NUMBER = 1;
    private static final Integer UPDATED_CHAPER_NUMBER = 2;
    private static final Integer SMALLER_CHAPER_NUMBER = 1 - 1;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/chapters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChapterRepository chapterRepository;

    @Mock
    private ChapterRepository chapterRepositoryMock;

    @Autowired
    private ChapterMapper chapterMapper;

    @Mock
    private ChapterService chapterServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChapterMockMvc;

    private Chapter chapter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chapter createEntity(EntityManager em) {
        Chapter chapter = new Chapter()
            .chapterId(DEFAULT_CHAPTER_ID)
            .chapterTitle(DEFAULT_CHAPTER_TITLE)
            .chapterDescription(DEFAULT_CHAPTER_DESCRIPTION)
            .chaperNumber(DEFAULT_CHAPER_NUMBER)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return chapter;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chapter createUpdatedEntity(EntityManager em) {
        Chapter chapter = new Chapter()
            .chapterId(UPDATED_CHAPTER_ID)
            .chapterTitle(UPDATED_CHAPTER_TITLE)
            .chapterDescription(UPDATED_CHAPTER_DESCRIPTION)
            .chaperNumber(UPDATED_CHAPER_NUMBER)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return chapter;
    }

    @BeforeEach
    public void initTest() {
        chapter = createEntity(em);
    }

    @Test
    @Transactional
    void createChapter() throws Exception {
        int databaseSizeBeforeCreate = chapterRepository.findAll().size();
        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);
        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chapterDTO)))
            .andExpect(status().isCreated());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeCreate + 1);
        Chapter testChapter = chapterList.get(chapterList.size() - 1);
        assertThat(testChapter.getChapterId()).isEqualTo(DEFAULT_CHAPTER_ID);
        assertThat(testChapter.getChapterTitle()).isEqualTo(DEFAULT_CHAPTER_TITLE);
        assertThat(testChapter.getChapterDescription()).isEqualTo(DEFAULT_CHAPTER_DESCRIPTION);
        assertThat(testChapter.getChaperNumber()).isEqualTo(DEFAULT_CHAPER_NUMBER);
        assertThat(testChapter.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testChapter.getEndDate()).isEqualTo(DEFAULT_END_DATE);
    }

    @Test
    @Transactional
    void createChapterWithExistingId() throws Exception {
        // Create the Chapter with an existing ID
        chapter.setId(1L);
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        int databaseSizeBeforeCreate = chapterRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkChapterIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = chapterRepository.findAll().size();
        // set the field null
        chapter.setChapterId(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChapterTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = chapterRepository.findAll().size();
        // set the field null
        chapter.setChapterTitle(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChapters() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapter.getId().intValue())))
            .andExpect(jsonPath("$.[*].chapterId").value(hasItem(DEFAULT_CHAPTER_ID)))
            .andExpect(jsonPath("$.[*].chapterTitle").value(hasItem(DEFAULT_CHAPTER_TITLE)))
            .andExpect(jsonPath("$.[*].chapterDescription").value(hasItem(DEFAULT_CHAPTER_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].chaperNumber").value(hasItem(DEFAULT_CHAPER_NUMBER)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChaptersWithEagerRelationshipsIsEnabled() throws Exception {
        when(chapterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(chapterServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChaptersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(chapterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(chapterRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getChapter() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get the chapter
        restChapterMockMvc
            .perform(get(ENTITY_API_URL_ID, chapter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chapter.getId().intValue()))
            .andExpect(jsonPath("$.chapterId").value(DEFAULT_CHAPTER_ID))
            .andExpect(jsonPath("$.chapterTitle").value(DEFAULT_CHAPTER_TITLE))
            .andExpect(jsonPath("$.chapterDescription").value(DEFAULT_CHAPTER_DESCRIPTION))
            .andExpect(jsonPath("$.chaperNumber").value(DEFAULT_CHAPER_NUMBER))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getChaptersByIdFiltering() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        Long id = chapter.getId();

        defaultChapterShouldBeFound("id.equals=" + id);
        defaultChapterShouldNotBeFound("id.notEquals=" + id);

        defaultChapterShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultChapterShouldNotBeFound("id.greaterThan=" + id);

        defaultChapterShouldBeFound("id.lessThanOrEqual=" + id);
        defaultChapterShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterIdIsEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterId equals to DEFAULT_CHAPTER_ID
        defaultChapterShouldBeFound("chapterId.equals=" + DEFAULT_CHAPTER_ID);

        // Get all the chapterList where chapterId equals to UPDATED_CHAPTER_ID
        defaultChapterShouldNotBeFound("chapterId.equals=" + UPDATED_CHAPTER_ID);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterIdIsInShouldWork() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterId in DEFAULT_CHAPTER_ID or UPDATED_CHAPTER_ID
        defaultChapterShouldBeFound("chapterId.in=" + DEFAULT_CHAPTER_ID + "," + UPDATED_CHAPTER_ID);

        // Get all the chapterList where chapterId equals to UPDATED_CHAPTER_ID
        defaultChapterShouldNotBeFound("chapterId.in=" + UPDATED_CHAPTER_ID);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterId is not null
        defaultChapterShouldBeFound("chapterId.specified=true");

        // Get all the chapterList where chapterId is null
        defaultChapterShouldNotBeFound("chapterId.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByChapterIdContainsSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterId contains DEFAULT_CHAPTER_ID
        defaultChapterShouldBeFound("chapterId.contains=" + DEFAULT_CHAPTER_ID);

        // Get all the chapterList where chapterId contains UPDATED_CHAPTER_ID
        defaultChapterShouldNotBeFound("chapterId.contains=" + UPDATED_CHAPTER_ID);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterIdNotContainsSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterId does not contain DEFAULT_CHAPTER_ID
        defaultChapterShouldNotBeFound("chapterId.doesNotContain=" + DEFAULT_CHAPTER_ID);

        // Get all the chapterList where chapterId does not contain UPDATED_CHAPTER_ID
        defaultChapterShouldBeFound("chapterId.doesNotContain=" + UPDATED_CHAPTER_ID);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterTitle equals to DEFAULT_CHAPTER_TITLE
        defaultChapterShouldBeFound("chapterTitle.equals=" + DEFAULT_CHAPTER_TITLE);

        // Get all the chapterList where chapterTitle equals to UPDATED_CHAPTER_TITLE
        defaultChapterShouldNotBeFound("chapterTitle.equals=" + UPDATED_CHAPTER_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterTitleIsInShouldWork() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterTitle in DEFAULT_CHAPTER_TITLE or UPDATED_CHAPTER_TITLE
        defaultChapterShouldBeFound("chapterTitle.in=" + DEFAULT_CHAPTER_TITLE + "," + UPDATED_CHAPTER_TITLE);

        // Get all the chapterList where chapterTitle equals to UPDATED_CHAPTER_TITLE
        defaultChapterShouldNotBeFound("chapterTitle.in=" + UPDATED_CHAPTER_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterTitle is not null
        defaultChapterShouldBeFound("chapterTitle.specified=true");

        // Get all the chapterList where chapterTitle is null
        defaultChapterShouldNotBeFound("chapterTitle.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByChapterTitleContainsSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterTitle contains DEFAULT_CHAPTER_TITLE
        defaultChapterShouldBeFound("chapterTitle.contains=" + DEFAULT_CHAPTER_TITLE);

        // Get all the chapterList where chapterTitle contains UPDATED_CHAPTER_TITLE
        defaultChapterShouldNotBeFound("chapterTitle.contains=" + UPDATED_CHAPTER_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterTitleNotContainsSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterTitle does not contain DEFAULT_CHAPTER_TITLE
        defaultChapterShouldNotBeFound("chapterTitle.doesNotContain=" + DEFAULT_CHAPTER_TITLE);

        // Get all the chapterList where chapterTitle does not contain UPDATED_CHAPTER_TITLE
        defaultChapterShouldBeFound("chapterTitle.doesNotContain=" + UPDATED_CHAPTER_TITLE);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterDescription equals to DEFAULT_CHAPTER_DESCRIPTION
        defaultChapterShouldBeFound("chapterDescription.equals=" + DEFAULT_CHAPTER_DESCRIPTION);

        // Get all the chapterList where chapterDescription equals to UPDATED_CHAPTER_DESCRIPTION
        defaultChapterShouldNotBeFound("chapterDescription.equals=" + UPDATED_CHAPTER_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterDescription in DEFAULT_CHAPTER_DESCRIPTION or UPDATED_CHAPTER_DESCRIPTION
        defaultChapterShouldBeFound("chapterDescription.in=" + DEFAULT_CHAPTER_DESCRIPTION + "," + UPDATED_CHAPTER_DESCRIPTION);

        // Get all the chapterList where chapterDescription equals to UPDATED_CHAPTER_DESCRIPTION
        defaultChapterShouldNotBeFound("chapterDescription.in=" + UPDATED_CHAPTER_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterDescription is not null
        defaultChapterShouldBeFound("chapterDescription.specified=true");

        // Get all the chapterList where chapterDescription is null
        defaultChapterShouldNotBeFound("chapterDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByChapterDescriptionContainsSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterDescription contains DEFAULT_CHAPTER_DESCRIPTION
        defaultChapterShouldBeFound("chapterDescription.contains=" + DEFAULT_CHAPTER_DESCRIPTION);

        // Get all the chapterList where chapterDescription contains UPDATED_CHAPTER_DESCRIPTION
        defaultChapterShouldNotBeFound("chapterDescription.contains=" + UPDATED_CHAPTER_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllChaptersByChapterDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chapterDescription does not contain DEFAULT_CHAPTER_DESCRIPTION
        defaultChapterShouldNotBeFound("chapterDescription.doesNotContain=" + DEFAULT_CHAPTER_DESCRIPTION);

        // Get all the chapterList where chapterDescription does not contain UPDATED_CHAPTER_DESCRIPTION
        defaultChapterShouldBeFound("chapterDescription.doesNotContain=" + UPDATED_CHAPTER_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllChaptersByChaperNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chaperNumber equals to DEFAULT_CHAPER_NUMBER
        defaultChapterShouldBeFound("chaperNumber.equals=" + DEFAULT_CHAPER_NUMBER);

        // Get all the chapterList where chaperNumber equals to UPDATED_CHAPER_NUMBER
        defaultChapterShouldNotBeFound("chaperNumber.equals=" + UPDATED_CHAPER_NUMBER);
    }

    @Test
    @Transactional
    void getAllChaptersByChaperNumberIsInShouldWork() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chaperNumber in DEFAULT_CHAPER_NUMBER or UPDATED_CHAPER_NUMBER
        defaultChapterShouldBeFound("chaperNumber.in=" + DEFAULT_CHAPER_NUMBER + "," + UPDATED_CHAPER_NUMBER);

        // Get all the chapterList where chaperNumber equals to UPDATED_CHAPER_NUMBER
        defaultChapterShouldNotBeFound("chaperNumber.in=" + UPDATED_CHAPER_NUMBER);
    }

    @Test
    @Transactional
    void getAllChaptersByChaperNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chaperNumber is not null
        defaultChapterShouldBeFound("chaperNumber.specified=true");

        // Get all the chapterList where chaperNumber is null
        defaultChapterShouldNotBeFound("chaperNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByChaperNumberIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chaperNumber is greater than or equal to DEFAULT_CHAPER_NUMBER
        defaultChapterShouldBeFound("chaperNumber.greaterThanOrEqual=" + DEFAULT_CHAPER_NUMBER);

        // Get all the chapterList where chaperNumber is greater than or equal to UPDATED_CHAPER_NUMBER
        defaultChapterShouldNotBeFound("chaperNumber.greaterThanOrEqual=" + UPDATED_CHAPER_NUMBER);
    }

    @Test
    @Transactional
    void getAllChaptersByChaperNumberIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chaperNumber is less than or equal to DEFAULT_CHAPER_NUMBER
        defaultChapterShouldBeFound("chaperNumber.lessThanOrEqual=" + DEFAULT_CHAPER_NUMBER);

        // Get all the chapterList where chaperNumber is less than or equal to SMALLER_CHAPER_NUMBER
        defaultChapterShouldNotBeFound("chaperNumber.lessThanOrEqual=" + SMALLER_CHAPER_NUMBER);
    }

    @Test
    @Transactional
    void getAllChaptersByChaperNumberIsLessThanSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chaperNumber is less than DEFAULT_CHAPER_NUMBER
        defaultChapterShouldNotBeFound("chaperNumber.lessThan=" + DEFAULT_CHAPER_NUMBER);

        // Get all the chapterList where chaperNumber is less than UPDATED_CHAPER_NUMBER
        defaultChapterShouldBeFound("chaperNumber.lessThan=" + UPDATED_CHAPER_NUMBER);
    }

    @Test
    @Transactional
    void getAllChaptersByChaperNumberIsGreaterThanSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where chaperNumber is greater than DEFAULT_CHAPER_NUMBER
        defaultChapterShouldNotBeFound("chaperNumber.greaterThan=" + DEFAULT_CHAPER_NUMBER);

        // Get all the chapterList where chaperNumber is greater than SMALLER_CHAPER_NUMBER
        defaultChapterShouldBeFound("chaperNumber.greaterThan=" + SMALLER_CHAPER_NUMBER);
    }

    @Test
    @Transactional
    void getAllChaptersByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where startDate equals to DEFAULT_START_DATE
        defaultChapterShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the chapterList where startDate equals to UPDATED_START_DATE
        defaultChapterShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultChapterShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the chapterList where startDate equals to UPDATED_START_DATE
        defaultChapterShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where startDate is not null
        defaultChapterShouldBeFound("startDate.specified=true");

        // Get all the chapterList where startDate is null
        defaultChapterShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultChapterShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the chapterList where startDate is greater than or equal to UPDATED_START_DATE
        defaultChapterShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where startDate is less than or equal to DEFAULT_START_DATE
        defaultChapterShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the chapterList where startDate is less than or equal to SMALLER_START_DATE
        defaultChapterShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where startDate is less than DEFAULT_START_DATE
        defaultChapterShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the chapterList where startDate is less than UPDATED_START_DATE
        defaultChapterShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where startDate is greater than DEFAULT_START_DATE
        defaultChapterShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the chapterList where startDate is greater than SMALLER_START_DATE
        defaultChapterShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where endDate equals to DEFAULT_END_DATE
        defaultChapterShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the chapterList where endDate equals to UPDATED_END_DATE
        defaultChapterShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultChapterShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the chapterList where endDate equals to UPDATED_END_DATE
        defaultChapterShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where endDate is not null
        defaultChapterShouldBeFound("endDate.specified=true");

        // Get all the chapterList where endDate is null
        defaultChapterShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllChaptersByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where endDate is greater than or equal to DEFAULT_END_DATE
        defaultChapterShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the chapterList where endDate is greater than or equal to UPDATED_END_DATE
        defaultChapterShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where endDate is less than or equal to DEFAULT_END_DATE
        defaultChapterShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the chapterList where endDate is less than or equal to SMALLER_END_DATE
        defaultChapterShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where endDate is less than DEFAULT_END_DATE
        defaultChapterShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the chapterList where endDate is less than UPDATED_END_DATE
        defaultChapterShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList where endDate is greater than DEFAULT_END_DATE
        defaultChapterShouldNotBeFound("endDate.greaterThan=" + DEFAULT_END_DATE);

        // Get all the chapterList where endDate is greater than SMALLER_END_DATE
        defaultChapterShouldBeFound("endDate.greaterThan=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    void getAllChaptersByContentIsEqualToSomething() throws Exception {
        Content content;
        if (TestUtil.findAll(em, Content.class).isEmpty()) {
            chapterRepository.saveAndFlush(chapter);
            content = ContentResourceIT.createEntity(em);
        } else {
            content = TestUtil.findAll(em, Content.class).get(0);
        }
        em.persist(content);
        em.flush();
        chapter.addContent(content);
        chapterRepository.saveAndFlush(chapter);
        Long contentId = content.getId();

        // Get all the chapterList where content equals to contentId
        defaultChapterShouldBeFound("contentId.equals=" + contentId);

        // Get all the chapterList where content equals to (contentId + 1)
        defaultChapterShouldNotBeFound("contentId.equals=" + (contentId + 1));
    }

    @Test
    @Transactional
    void getAllChaptersByCourseIsEqualToSomething() throws Exception {
        Course course;
        if (TestUtil.findAll(em, Course.class).isEmpty()) {
            chapterRepository.saveAndFlush(chapter);
            course = CourseResourceIT.createEntity(em);
        } else {
            course = TestUtil.findAll(em, Course.class).get(0);
        }
        em.persist(course);
        em.flush();
        chapter.setCourse(course);
        chapterRepository.saveAndFlush(chapter);
        Long courseId = course.getId();

        // Get all the chapterList where course equals to courseId
        defaultChapterShouldBeFound("courseId.equals=" + courseId);

        // Get all the chapterList where course equals to (courseId + 1)
        defaultChapterShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultChapterShouldBeFound(String filter) throws Exception {
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapter.getId().intValue())))
            .andExpect(jsonPath("$.[*].chapterId").value(hasItem(DEFAULT_CHAPTER_ID)))
            .andExpect(jsonPath("$.[*].chapterTitle").value(hasItem(DEFAULT_CHAPTER_TITLE)))
            .andExpect(jsonPath("$.[*].chapterDescription").value(hasItem(DEFAULT_CHAPTER_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].chaperNumber").value(hasItem(DEFAULT_CHAPER_NUMBER)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));

        // Check, that the count call also returns 1
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultChapterShouldNotBeFound(String filter) throws Exception {
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingChapter() throws Exception {
        // Get the chapter
        restChapterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChapter() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();

        // Update the chapter
        Chapter updatedChapter = chapterRepository.findById(chapter.getId()).get();
        // Disconnect from session so that the updates on updatedChapter are not directly saved in db
        em.detach(updatedChapter);
        updatedChapter
            .chapterId(UPDATED_CHAPTER_ID)
            .chapterTitle(UPDATED_CHAPTER_TITLE)
            .chapterDescription(UPDATED_CHAPTER_DESCRIPTION)
            .chaperNumber(UPDATED_CHAPER_NUMBER)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        ChapterDTO chapterDTO = chapterMapper.toDto(updatedChapter);

        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(chapterDTO))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
        Chapter testChapter = chapterList.get(chapterList.size() - 1);
        assertThat(testChapter.getChapterId()).isEqualTo(UPDATED_CHAPTER_ID);
        assertThat(testChapter.getChapterTitle()).isEqualTo(UPDATED_CHAPTER_TITLE);
        assertThat(testChapter.getChapterDescription()).isEqualTo(UPDATED_CHAPTER_DESCRIPTION);
        assertThat(testChapter.getChaperNumber()).isEqualTo(UPDATED_CHAPER_NUMBER);
        assertThat(testChapter.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testChapter.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void putNonExistingChapter() throws Exception {
        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();
        chapter.setId(count.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChapter() throws Exception {
        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();
        chapter.setId(count.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChapter() throws Exception {
        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();
        chapter.setId(count.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chapterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChapterWithPatch() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();

        // Update the chapter using partial update
        Chapter partialUpdatedChapter = new Chapter();
        partialUpdatedChapter.setId(chapter.getId());

        partialUpdatedChapter
            .chapterId(UPDATED_CHAPTER_ID)
            .chapterTitle(UPDATED_CHAPTER_TITLE)
            .chapterDescription(UPDATED_CHAPTER_DESCRIPTION)
            .chaperNumber(UPDATED_CHAPER_NUMBER)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapter.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedChapter))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
        Chapter testChapter = chapterList.get(chapterList.size() - 1);
        assertThat(testChapter.getChapterId()).isEqualTo(UPDATED_CHAPTER_ID);
        assertThat(testChapter.getChapterTitle()).isEqualTo(UPDATED_CHAPTER_TITLE);
        assertThat(testChapter.getChapterDescription()).isEqualTo(UPDATED_CHAPTER_DESCRIPTION);
        assertThat(testChapter.getChaperNumber()).isEqualTo(UPDATED_CHAPER_NUMBER);
        assertThat(testChapter.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testChapter.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void fullUpdateChapterWithPatch() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();

        // Update the chapter using partial update
        Chapter partialUpdatedChapter = new Chapter();
        partialUpdatedChapter.setId(chapter.getId());

        partialUpdatedChapter
            .chapterId(UPDATED_CHAPTER_ID)
            .chapterTitle(UPDATED_CHAPTER_TITLE)
            .chapterDescription(UPDATED_CHAPTER_DESCRIPTION)
            .chaperNumber(UPDATED_CHAPER_NUMBER)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapter.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedChapter))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
        Chapter testChapter = chapterList.get(chapterList.size() - 1);
        assertThat(testChapter.getChapterId()).isEqualTo(UPDATED_CHAPTER_ID);
        assertThat(testChapter.getChapterTitle()).isEqualTo(UPDATED_CHAPTER_TITLE);
        assertThat(testChapter.getChapterDescription()).isEqualTo(UPDATED_CHAPTER_DESCRIPTION);
        assertThat(testChapter.getChaperNumber()).isEqualTo(UPDATED_CHAPER_NUMBER);
        assertThat(testChapter.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testChapter.getEndDate()).isEqualTo(UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingChapter() throws Exception {
        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();
        chapter.setId(count.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chapterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChapter() throws Exception {
        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();
        chapter.setId(count.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChapter() throws Exception {
        int databaseSizeBeforeUpdate = chapterRepository.findAll().size();
        chapter.setId(count.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(chapterDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chapter in the database
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChapter() throws Exception {
        // Initialize the database
        chapterRepository.saveAndFlush(chapter);

        int databaseSizeBeforeDelete = chapterRepository.findAll().size();

        // Delete the chapter
        restChapterMockMvc
            .perform(delete(ENTITY_API_URL_ID, chapter.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Chapter> chapterList = chapterRepository.findAll();
        assertThat(chapterList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
