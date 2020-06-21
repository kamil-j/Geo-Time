package pl.edu.agh.geotime.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.domain.StudyField;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.StudyFieldRepository;
import pl.edu.agh.geotime.service.StudyFieldService;
import pl.edu.agh.geotime.web.rest.mapper.StudyFieldMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the StudyFieldResource REST controller.
 *
 * @see StudyFieldResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class StudyFieldResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudyFieldService studyFieldService;

    @Autowired
    private StudyFieldMapper studyFieldMapper;

    @Autowired
    private StudyFieldRepository studyFieldRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restStudyFieldMockMvc;

    private StudyField studyField;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StudyFieldResource studyFieldResource = new StudyFieldResource(studyFieldService, studyFieldMapper);
        this.restStudyFieldMockMvc = MockMvcBuilders.standaloneSetup(studyFieldResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static StudyField createEntity(EntityManager em) {
        return new StudyField()
            .name(DEFAULT_NAME)
            .shortName(DEFAULT_SHORT_NAME);
    }

    @Before
    public void initTest() {
        studyField = createEntity(em);

        Department department = departmentRepository.findOne(1L);
        studyField.setDepartment(department);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createStudyField() throws Exception {
        int databaseSizeBeforeCreate = studyFieldRepository.findAll().size();

        // Create the StudyField
        restStudyFieldMockMvc.perform(post("/api/study-fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studyFieldMapper.toDto(studyField))))
            .andExpect(status().isCreated());

        // Validate the StudyField in the database
        List<StudyField> studyFieldList = studyFieldRepository.findAll();
        assertThat(studyFieldList).hasSize(databaseSizeBeforeCreate + 1);
        StudyField testStudyField = studyFieldList.get(studyFieldList.size() - 1);
        assertThat(testStudyField.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStudyField.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createStudyFieldWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = studyFieldRepository.findAll().size();

        // Create the StudyField with an existing ID
        studyField.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudyFieldMockMvc.perform(post("/api/study-fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studyFieldMapper.toDto(studyField))))
            .andExpect(status().isBadRequest());

        // Validate the StudyField in the database
        List<StudyField> studyFieldList = studyFieldRepository.findAll();
        assertThat(studyFieldList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = studyFieldRepository.findAll().size();
        // set the field null
        studyField.setName(null);

        // Create the StudyField, which fails.

        restStudyFieldMockMvc.perform(post("/api/study-fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studyFieldMapper.toDto(studyField))))
            .andExpect(status().isBadRequest());

        List<StudyField> studyFieldList = studyFieldRepository.findAll();
        assertThat(studyFieldList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkShortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = studyFieldRepository.findAll().size();
        // set the field null
        studyField.setShortName(null);

        // Create the StudyField, which fails.

        restStudyFieldMockMvc.perform(post("/api/study-fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studyFieldMapper.toDto(studyField))))
            .andExpect(status().isBadRequest());

        List<StudyField> studyFieldList = studyFieldRepository.findAll();
        assertThat(studyFieldList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllStudyFields() throws Exception {
        // Initialize the database
        studyFieldRepository.saveAndFlush(studyField);

        // Get all the studyFieldList
        restStudyFieldMockMvc.perform(get("/api/study-fields?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(studyField.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getStudyField() throws Exception {
        // Initialize the database
        studyFieldRepository.saveAndFlush(studyField);

        // Get the studyField
        restStudyFieldMockMvc.perform(get("/api/study-fields/{id}", studyField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(studyField.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingStudyField() throws Exception {
        // Get the studyField
        restStudyFieldMockMvc.perform(get("/api/study-fields/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateStudyField() throws Exception {
        // Initialize the database
        studyFieldRepository.saveAndFlush(studyField);
        int databaseSizeBeforeUpdate = studyFieldRepository.findAll().size();

        // Update the studyField
        StudyField updatedStudyField = studyFieldRepository.findOne(studyField.getId());
        // Disconnect from session so that the updates on updatedStudyField are not directly saved in db
        em.detach(updatedStudyField);
        updatedStudyField
            .name(UPDATED_NAME)
            .shortName(UPDATED_SHORT_NAME);

        restStudyFieldMockMvc.perform(put("/api/study-fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studyFieldMapper.toDto(updatedStudyField))))
            .andExpect(status().isOk());

        // Validate the StudyField in the database
        List<StudyField> studyFieldList = studyFieldRepository.findAll();
        assertThat(studyFieldList).hasSize(databaseSizeBeforeUpdate);
        StudyField testStudyField = studyFieldList.get(studyFieldList.size() - 1);
        assertThat(testStudyField.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStudyField.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingStudyField() throws Exception {
        int databaseSizeBeforeUpdate = studyFieldRepository.findAll().size();

        // Create the StudyField

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restStudyFieldMockMvc.perform(put("/api/study-fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(studyFieldMapper.toDto(studyField))))
            .andExpect(status().isCreated());

        // Validate the StudyField in the database
        List<StudyField> studyFieldList = studyFieldRepository.findAll();
        assertThat(studyFieldList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteStudyField() throws Exception {
        // Initialize the database
        studyFieldRepository.saveAndFlush(studyField);
        int databaseSizeBeforeDelete = studyFieldRepository.findAll().size();

        // Get the studyField
        restStudyFieldMockMvc.perform(delete("/api/study-fields/{id}", studyField.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<StudyField> studyFieldList = studyFieldRepository.findAll();
        assertThat(studyFieldList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudyField.class);
        StudyField studyField1 = new StudyField();
        studyField1.setId(1L);
        StudyField studyField2 = new StudyField();
        studyField2.setId(studyField1.getId());
        assertThat(studyField1).isEqualTo(studyField2);
        studyField2.setId(2L);
        assertThat(studyField1).isNotEqualTo(studyField2);
        studyField1.setId(null);
        assertThat(studyField1).isNotEqualTo(studyField2);
    }
}
