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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.ClassType;
import pl.edu.agh.geotime.repository.ClassTypeRepository;
import pl.edu.agh.geotime.service.ClassTypeService;
import pl.edu.agh.geotime.web.rest.mapper.ClassTypeMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the ClassTypeResource REST controller.
 *
 * @see ClassTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class ClassTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    @Autowired
    private ClassTypeRepository classTypeRepository;

    @Autowired
    private ClassTypeMapper classTypeMapper;

    @Autowired
    private ClassTypeService classTypeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restClassTypeMockMvc;

    private ClassType classType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClassTypeResource classTypeResource = new ClassTypeResource(classTypeService, classTypeMapper);
        this.restClassTypeMockMvc = MockMvcBuilders.standaloneSetup(classTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassType createEntity(EntityManager em) {
        return new ClassType()
            .name(DEFAULT_NAME)
            .shortName(DEFAULT_SHORT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .color(DEFAULT_COLOR);
    }

    @Before
    public void initTest() {
        classType = createEntity(em);
    }

    @Test
    @Transactional
    public void createClassType() throws Exception {
        int databaseSizeBeforeCreate = classTypeRepository.findAll().size();

        // Create the ClassType
        restClassTypeMockMvc.perform(post("/api/class-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classTypeMapper.toDto(classType))))
            .andExpect(status().isCreated());

        // Validate the ClassType in the database
        List<ClassType> classTypeList = classTypeRepository.findAll();
        assertThat(classTypeList).hasSize(databaseSizeBeforeCreate + 1);
        ClassType testClassType = classTypeList.get(classTypeList.size() - 1);
        assertThat(testClassType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClassType.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
        assertThat(testClassType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testClassType.getColor()).isEqualTo(DEFAULT_COLOR);
    }

    @Test
    @Transactional
    public void createClassTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = classTypeRepository.findAll().size();

        // Create the ClassType with an existing ID
        classType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassTypeMockMvc.perform(post("/api/class-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classTypeMapper.toDto(classType))))
            .andExpect(status().isBadRequest());

        // Validate the ClassType in the database
        List<ClassType> classTypeList = classTypeRepository.findAll();
        assertThat(classTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = classTypeRepository.findAll().size();
        // set the field null
        classType.setName(null);

        // Create the ClassType, which fails.

        restClassTypeMockMvc.perform(post("/api/class-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classTypeMapper.toDto(classType))))
            .andExpect(status().isBadRequest());

        List<ClassType> classTypeList = classTypeRepository.findAll();
        assertThat(classTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkShortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = classTypeRepository.findAll().size();
        // set the field null
        classType.setShortName(null);

        // Create the ClassType, which fails.

        restClassTypeMockMvc.perform(post("/api/class-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classTypeMapper.toDto(classType))))
            .andExpect(status().isBadRequest());

        List<ClassType> classTypeList = classTypeRepository.findAll();
        assertThat(classTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClassTypes() throws Exception {
        // Initialize the database
        classTypeRepository.saveAndFlush(classType);

        // Get all the classTypeList
        restClassTypeMockMvc.perform(get("/api/class-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)));
    }

    @Test
    @Transactional
    public void getClassType() throws Exception {
        // Initialize the database
        classTypeRepository.saveAndFlush(classType);

        // Get the classType
        restClassTypeMockMvc.perform(get("/api/class-types/{id}", classType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(classType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR));
    }

    @Test
    @Transactional
    public void getNonExistingClassType() throws Exception {
        // Get the classType
        restClassTypeMockMvc.perform(get("/api/class-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClassType() throws Exception {
        // Initialize the database
        classTypeRepository.saveAndFlush(classType);
        int databaseSizeBeforeUpdate = classTypeRepository.findAll().size();

        // Update the classType
        ClassType updatedClassType = classTypeRepository.findOne(classType.getId());
        // Disconnect from session so that the updates on updatedClassType are not directly saved in db
        em.detach(updatedClassType);
        updatedClassType
            .name(UPDATED_NAME)
            .shortName(UPDATED_SHORT_NAME)
            .description(UPDATED_DESCRIPTION)
            .color(UPDATED_COLOR);

        restClassTypeMockMvc.perform(put("/api/class-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classTypeMapper.toDto(updatedClassType))))
            .andExpect(status().isOk());

        // Validate the ClassType in the database
        List<ClassType> classTypeList = classTypeRepository.findAll();
        assertThat(classTypeList).hasSize(databaseSizeBeforeUpdate);
        ClassType testClassType = classTypeList.get(classTypeList.size() - 1);
        assertThat(testClassType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClassType.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
        assertThat(testClassType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testClassType.getColor()).isEqualTo(UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void updateNonExistingClassType() throws Exception {
        int databaseSizeBeforeUpdate = classTypeRepository.findAll().size();

        // Create the ClassType

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restClassTypeMockMvc.perform(put("/api/class-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classTypeMapper.toDto(classType))))
            .andExpect(status().isCreated());

        // Validate the ClassType in the database
        List<ClassType> classTypeList = classTypeRepository.findAll();
        assertThat(classTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteClassType() throws Exception {
        // Initialize the database
        classTypeRepository.saveAndFlush(classType);
        int databaseSizeBeforeDelete = classTypeRepository.findAll().size();

        // Get the classType
        restClassTypeMockMvc.perform(delete("/api/class-types/{id}", classType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ClassType> classTypeList = classTypeRepository.findAll();
        assertThat(classTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassType.class);
        ClassType classType1 = new ClassType();
        classType1.setId(1L);
        ClassType classType2 = new ClassType();
        classType2.setId(classType1.getId());
        assertThat(classType1).isEqualTo(classType2);
        classType2.setId(2L);
        assertThat(classType1).isNotEqualTo(classType2);
        classType1.setId(null);
        assertThat(classType1).isNotEqualTo(classType2);
    }
}
