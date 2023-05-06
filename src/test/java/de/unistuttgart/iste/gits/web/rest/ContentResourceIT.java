package de.unistuttgart.iste.gits.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.unistuttgart.iste.gits.IntegrationTest;
import de.unistuttgart.iste.gits.domain.Chapter;
import de.unistuttgart.iste.gits.domain.Content;
import de.unistuttgart.iste.gits.repository.ContentRepository;
import de.unistuttgart.iste.gits.service.ContentService;
import de.unistuttgart.iste.gits.service.criteria.ContentCriteria;
import de.unistuttgart.iste.gits.service.dto.ContentDTO;
import de.unistuttgart.iste.gits.service.mapper.ContentMapper;
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
 * Integration tests for the {@link ContentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ContentResourceIT {

    private static final String DEFAULT_CONTENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_REWARD_POINTS = 1;
    private static final Integer UPDATED_REWARD_POINTS = 2;
    private static final Integer SMALLER_REWARD_POINTS = 1 - 1;

    private static final Boolean DEFAULT_WORKED_ON = false;
    private static final Boolean UPDATED_WORKED_ON = true;

    private static final String ENTITY_API_URL = "/api/contents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ContentRepository contentRepository;

    @Mock
    private ContentRepository contentRepositoryMock;

    @Autowired
    private ContentMapper contentMapper;

    @Mock
    private ContentService contentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContentMockMvc;

    private Content content;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Content createEntity(EntityManager em) {
        Content content = new Content()
            .contentId(DEFAULT_CONTENT_ID)
            .contentName(DEFAULT_CONTENT_NAME)
            .rewardPoints(DEFAULT_REWARD_POINTS)
            .workedOn(DEFAULT_WORKED_ON);
        return content;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Content createUpdatedEntity(EntityManager em) {
        Content content = new Content()
            .contentId(UPDATED_CONTENT_ID)
            .contentName(UPDATED_CONTENT_NAME)
            .rewardPoints(UPDATED_REWARD_POINTS)
            .workedOn(UPDATED_WORKED_ON);
        return content;
    }

    @BeforeEach
    public void initTest() {
        content = createEntity(em);
    }

    @Test
    @Transactional
    void createContent() throws Exception {
        int databaseSizeBeforeCreate = contentRepository.findAll().size();
        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);
        restContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contentDTO)))
            .andExpect(status().isCreated());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeCreate + 1);
        Content testContent = contentList.get(contentList.size() - 1);
        assertThat(testContent.getContentId()).isEqualTo(DEFAULT_CONTENT_ID);
        assertThat(testContent.getContentName()).isEqualTo(DEFAULT_CONTENT_NAME);
        assertThat(testContent.getRewardPoints()).isEqualTo(DEFAULT_REWARD_POINTS);
        assertThat(testContent.getWorkedOn()).isEqualTo(DEFAULT_WORKED_ON);
    }

    @Test
    @Transactional
    void createContentWithExistingId() throws Exception {
        // Create the Content with an existing ID
        content.setId(1L);
        ContentDTO contentDTO = contentMapper.toDto(content);

        int databaseSizeBeforeCreate = contentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = contentRepository.findAll().size();
        // set the field null
        content.setContentId(null);

        // Create the Content, which fails.
        ContentDTO contentDTO = contentMapper.toDto(content);

        restContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contentDTO)))
            .andExpect(status().isBadRequest());

        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = contentRepository.findAll().size();
        // set the field null
        content.setContentName(null);

        // Create the Content, which fails.
        ContentDTO contentDTO = contentMapper.toDto(content);

        restContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contentDTO)))
            .andExpect(status().isBadRequest());

        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContents() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().intValue())))
            .andExpect(jsonPath("$.[*].contentId").value(hasItem(DEFAULT_CONTENT_ID)))
            .andExpect(jsonPath("$.[*].contentName").value(hasItem(DEFAULT_CONTENT_NAME)))
            .andExpect(jsonPath("$.[*].rewardPoints").value(hasItem(DEFAULT_REWARD_POINTS)))
            .andExpect(jsonPath("$.[*].workedOn").value(hasItem(DEFAULT_WORKED_ON.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(contentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(contentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(contentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(contentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get the content
        restContentMockMvc
            .perform(get(ENTITY_API_URL_ID, content.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(content.getId().intValue()))
            .andExpect(jsonPath("$.contentId").value(DEFAULT_CONTENT_ID))
            .andExpect(jsonPath("$.contentName").value(DEFAULT_CONTENT_NAME))
            .andExpect(jsonPath("$.rewardPoints").value(DEFAULT_REWARD_POINTS))
            .andExpect(jsonPath("$.workedOn").value(DEFAULT_WORKED_ON.booleanValue()));
    }

    @Test
    @Transactional
    void getContentsByIdFiltering() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        Long id = content.getId();

        defaultContentShouldBeFound("id.equals=" + id);
        defaultContentShouldNotBeFound("id.notEquals=" + id);

        defaultContentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultContentShouldNotBeFound("id.greaterThan=" + id);

        defaultContentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultContentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllContentsByContentIdIsEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentId equals to DEFAULT_CONTENT_ID
        defaultContentShouldBeFound("contentId.equals=" + DEFAULT_CONTENT_ID);

        // Get all the contentList where contentId equals to UPDATED_CONTENT_ID
        defaultContentShouldNotBeFound("contentId.equals=" + UPDATED_CONTENT_ID);
    }

    @Test
    @Transactional
    void getAllContentsByContentIdIsInShouldWork() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentId in DEFAULT_CONTENT_ID or UPDATED_CONTENT_ID
        defaultContentShouldBeFound("contentId.in=" + DEFAULT_CONTENT_ID + "," + UPDATED_CONTENT_ID);

        // Get all the contentList where contentId equals to UPDATED_CONTENT_ID
        defaultContentShouldNotBeFound("contentId.in=" + UPDATED_CONTENT_ID);
    }

    @Test
    @Transactional
    void getAllContentsByContentIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentId is not null
        defaultContentShouldBeFound("contentId.specified=true");

        // Get all the contentList where contentId is null
        defaultContentShouldNotBeFound("contentId.specified=false");
    }

    @Test
    @Transactional
    void getAllContentsByContentIdContainsSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentId contains DEFAULT_CONTENT_ID
        defaultContentShouldBeFound("contentId.contains=" + DEFAULT_CONTENT_ID);

        // Get all the contentList where contentId contains UPDATED_CONTENT_ID
        defaultContentShouldNotBeFound("contentId.contains=" + UPDATED_CONTENT_ID);
    }

    @Test
    @Transactional
    void getAllContentsByContentIdNotContainsSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentId does not contain DEFAULT_CONTENT_ID
        defaultContentShouldNotBeFound("contentId.doesNotContain=" + DEFAULT_CONTENT_ID);

        // Get all the contentList where contentId does not contain UPDATED_CONTENT_ID
        defaultContentShouldBeFound("contentId.doesNotContain=" + UPDATED_CONTENT_ID);
    }

    @Test
    @Transactional
    void getAllContentsByContentNameIsEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentName equals to DEFAULT_CONTENT_NAME
        defaultContentShouldBeFound("contentName.equals=" + DEFAULT_CONTENT_NAME);

        // Get all the contentList where contentName equals to UPDATED_CONTENT_NAME
        defaultContentShouldNotBeFound("contentName.equals=" + UPDATED_CONTENT_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByContentNameIsInShouldWork() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentName in DEFAULT_CONTENT_NAME or UPDATED_CONTENT_NAME
        defaultContentShouldBeFound("contentName.in=" + DEFAULT_CONTENT_NAME + "," + UPDATED_CONTENT_NAME);

        // Get all the contentList where contentName equals to UPDATED_CONTENT_NAME
        defaultContentShouldNotBeFound("contentName.in=" + UPDATED_CONTENT_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByContentNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentName is not null
        defaultContentShouldBeFound("contentName.specified=true");

        // Get all the contentList where contentName is null
        defaultContentShouldNotBeFound("contentName.specified=false");
    }

    @Test
    @Transactional
    void getAllContentsByContentNameContainsSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentName contains DEFAULT_CONTENT_NAME
        defaultContentShouldBeFound("contentName.contains=" + DEFAULT_CONTENT_NAME);

        // Get all the contentList where contentName contains UPDATED_CONTENT_NAME
        defaultContentShouldNotBeFound("contentName.contains=" + UPDATED_CONTENT_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByContentNameNotContainsSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where contentName does not contain DEFAULT_CONTENT_NAME
        defaultContentShouldNotBeFound("contentName.doesNotContain=" + DEFAULT_CONTENT_NAME);

        // Get all the contentList where contentName does not contain UPDATED_CONTENT_NAME
        defaultContentShouldBeFound("contentName.doesNotContain=" + UPDATED_CONTENT_NAME);
    }

    @Test
    @Transactional
    void getAllContentsByRewardPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where rewardPoints equals to DEFAULT_REWARD_POINTS
        defaultContentShouldBeFound("rewardPoints.equals=" + DEFAULT_REWARD_POINTS);

        // Get all the contentList where rewardPoints equals to UPDATED_REWARD_POINTS
        defaultContentShouldNotBeFound("rewardPoints.equals=" + UPDATED_REWARD_POINTS);
    }

    @Test
    @Transactional
    void getAllContentsByRewardPointsIsInShouldWork() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where rewardPoints in DEFAULT_REWARD_POINTS or UPDATED_REWARD_POINTS
        defaultContentShouldBeFound("rewardPoints.in=" + DEFAULT_REWARD_POINTS + "," + UPDATED_REWARD_POINTS);

        // Get all the contentList where rewardPoints equals to UPDATED_REWARD_POINTS
        defaultContentShouldNotBeFound("rewardPoints.in=" + UPDATED_REWARD_POINTS);
    }

    @Test
    @Transactional
    void getAllContentsByRewardPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where rewardPoints is not null
        defaultContentShouldBeFound("rewardPoints.specified=true");

        // Get all the contentList where rewardPoints is null
        defaultContentShouldNotBeFound("rewardPoints.specified=false");
    }

    @Test
    @Transactional
    void getAllContentsByRewardPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where rewardPoints is greater than or equal to DEFAULT_REWARD_POINTS
        defaultContentShouldBeFound("rewardPoints.greaterThanOrEqual=" + DEFAULT_REWARD_POINTS);

        // Get all the contentList where rewardPoints is greater than or equal to UPDATED_REWARD_POINTS
        defaultContentShouldNotBeFound("rewardPoints.greaterThanOrEqual=" + UPDATED_REWARD_POINTS);
    }

    @Test
    @Transactional
    void getAllContentsByRewardPointsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where rewardPoints is less than or equal to DEFAULT_REWARD_POINTS
        defaultContentShouldBeFound("rewardPoints.lessThanOrEqual=" + DEFAULT_REWARD_POINTS);

        // Get all the contentList where rewardPoints is less than or equal to SMALLER_REWARD_POINTS
        defaultContentShouldNotBeFound("rewardPoints.lessThanOrEqual=" + SMALLER_REWARD_POINTS);
    }

    @Test
    @Transactional
    void getAllContentsByRewardPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where rewardPoints is less than DEFAULT_REWARD_POINTS
        defaultContentShouldNotBeFound("rewardPoints.lessThan=" + DEFAULT_REWARD_POINTS);

        // Get all the contentList where rewardPoints is less than UPDATED_REWARD_POINTS
        defaultContentShouldBeFound("rewardPoints.lessThan=" + UPDATED_REWARD_POINTS);
    }

    @Test
    @Transactional
    void getAllContentsByRewardPointsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where rewardPoints is greater than DEFAULT_REWARD_POINTS
        defaultContentShouldNotBeFound("rewardPoints.greaterThan=" + DEFAULT_REWARD_POINTS);

        // Get all the contentList where rewardPoints is greater than SMALLER_REWARD_POINTS
        defaultContentShouldBeFound("rewardPoints.greaterThan=" + SMALLER_REWARD_POINTS);
    }

    @Test
    @Transactional
    void getAllContentsByWorkedOnIsEqualToSomething() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where workedOn equals to DEFAULT_WORKED_ON
        defaultContentShouldBeFound("workedOn.equals=" + DEFAULT_WORKED_ON);

        // Get all the contentList where workedOn equals to UPDATED_WORKED_ON
        defaultContentShouldNotBeFound("workedOn.equals=" + UPDATED_WORKED_ON);
    }

    @Test
    @Transactional
    void getAllContentsByWorkedOnIsInShouldWork() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where workedOn in DEFAULT_WORKED_ON or UPDATED_WORKED_ON
        defaultContentShouldBeFound("workedOn.in=" + DEFAULT_WORKED_ON + "," + UPDATED_WORKED_ON);

        // Get all the contentList where workedOn equals to UPDATED_WORKED_ON
        defaultContentShouldNotBeFound("workedOn.in=" + UPDATED_WORKED_ON);
    }

    @Test
    @Transactional
    void getAllContentsByWorkedOnIsNullOrNotNull() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        // Get all the contentList where workedOn is not null
        defaultContentShouldBeFound("workedOn.specified=true");

        // Get all the contentList where workedOn is null
        defaultContentShouldNotBeFound("workedOn.specified=false");
    }

    @Test
    @Transactional
    void getAllContentsByChapterIsEqualToSomething() throws Exception {
        Chapter chapter;
        if (TestUtil.findAll(em, Chapter.class).isEmpty()) {
            contentRepository.saveAndFlush(content);
            chapter = ChapterResourceIT.createEntity(em);
        } else {
            chapter = TestUtil.findAll(em, Chapter.class).get(0);
        }
        em.persist(chapter);
        em.flush();
        content.setChapter(chapter);
        contentRepository.saveAndFlush(content);
        Long chapterId = chapter.getId();

        // Get all the contentList where chapter equals to chapterId
        defaultContentShouldBeFound("chapterId.equals=" + chapterId);

        // Get all the contentList where chapter equals to (chapterId + 1)
        defaultContentShouldNotBeFound("chapterId.equals=" + (chapterId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContentShouldBeFound(String filter) throws Exception {
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(content.getId().intValue())))
            .andExpect(jsonPath("$.[*].contentId").value(hasItem(DEFAULT_CONTENT_ID)))
            .andExpect(jsonPath("$.[*].contentName").value(hasItem(DEFAULT_CONTENT_NAME)))
            .andExpect(jsonPath("$.[*].rewardPoints").value(hasItem(DEFAULT_REWARD_POINTS)))
            .andExpect(jsonPath("$.[*].workedOn").value(hasItem(DEFAULT_WORKED_ON.booleanValue())));

        // Check, that the count call also returns 1
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultContentShouldNotBeFound(String filter) throws Exception {
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restContentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingContent() throws Exception {
        // Get the content
        restContentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        int databaseSizeBeforeUpdate = contentRepository.findAll().size();

        // Update the content
        Content updatedContent = contentRepository.findById(content.getId()).get();
        // Disconnect from session so that the updates on updatedContent are not directly saved in db
        em.detach(updatedContent);
        updatedContent
            .contentId(UPDATED_CONTENT_ID)
            .contentName(UPDATED_CONTENT_NAME)
            .rewardPoints(UPDATED_REWARD_POINTS)
            .workedOn(UPDATED_WORKED_ON);
        ContentDTO contentDTO = contentMapper.toDto(updatedContent);

        restContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
        Content testContent = contentList.get(contentList.size() - 1);
        assertThat(testContent.getContentId()).isEqualTo(UPDATED_CONTENT_ID);
        assertThat(testContent.getContentName()).isEqualTo(UPDATED_CONTENT_NAME);
        assertThat(testContent.getRewardPoints()).isEqualTo(UPDATED_REWARD_POINTS);
        assertThat(testContent.getWorkedOn()).isEqualTo(UPDATED_WORKED_ON);
    }

    @Test
    @Transactional
    void putNonExistingContent() throws Exception {
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();
        content.setId(count.incrementAndGet());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContent() throws Exception {
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();
        content.setId(count.incrementAndGet());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContent() throws Exception {
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();
        content.setId(count.incrementAndGet());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContentWithPatch() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        int databaseSizeBeforeUpdate = contentRepository.findAll().size();

        // Update the content using partial update
        Content partialUpdatedContent = new Content();
        partialUpdatedContent.setId(content.getId());

        partialUpdatedContent.contentId(UPDATED_CONTENT_ID).rewardPoints(UPDATED_REWARD_POINTS).workedOn(UPDATED_WORKED_ON);

        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContent))
            )
            .andExpect(status().isOk());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
        Content testContent = contentList.get(contentList.size() - 1);
        assertThat(testContent.getContentId()).isEqualTo(UPDATED_CONTENT_ID);
        assertThat(testContent.getContentName()).isEqualTo(DEFAULT_CONTENT_NAME);
        assertThat(testContent.getRewardPoints()).isEqualTo(UPDATED_REWARD_POINTS);
        assertThat(testContent.getWorkedOn()).isEqualTo(UPDATED_WORKED_ON);
    }

    @Test
    @Transactional
    void fullUpdateContentWithPatch() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        int databaseSizeBeforeUpdate = contentRepository.findAll().size();

        // Update the content using partial update
        Content partialUpdatedContent = new Content();
        partialUpdatedContent.setId(content.getId());

        partialUpdatedContent
            .contentId(UPDATED_CONTENT_ID)
            .contentName(UPDATED_CONTENT_NAME)
            .rewardPoints(UPDATED_REWARD_POINTS)
            .workedOn(UPDATED_WORKED_ON);

        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContent))
            )
            .andExpect(status().isOk());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
        Content testContent = contentList.get(contentList.size() - 1);
        assertThat(testContent.getContentId()).isEqualTo(UPDATED_CONTENT_ID);
        assertThat(testContent.getContentName()).isEqualTo(UPDATED_CONTENT_NAME);
        assertThat(testContent.getRewardPoints()).isEqualTo(UPDATED_REWARD_POINTS);
        assertThat(testContent.getWorkedOn()).isEqualTo(UPDATED_WORKED_ON);
    }

    @Test
    @Transactional
    void patchNonExistingContent() throws Exception {
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();
        content.setId(count.incrementAndGet());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContent() throws Exception {
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();
        content.setId(count.incrementAndGet());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContent() throws Exception {
        int databaseSizeBeforeUpdate = contentRepository.findAll().size();
        content.setId(count.incrementAndGet());

        // Create the Content
        ContentDTO contentDTO = contentMapper.toDto(content);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(contentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Content in the database
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContent() throws Exception {
        // Initialize the database
        contentRepository.saveAndFlush(content);

        int databaseSizeBeforeDelete = contentRepository.findAll().size();

        // Delete the content
        restContentMockMvc
            .perform(delete(ENTITY_API_URL_ID, content.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Content> contentList = contentRepository.findAll();
        assertThat(contentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
