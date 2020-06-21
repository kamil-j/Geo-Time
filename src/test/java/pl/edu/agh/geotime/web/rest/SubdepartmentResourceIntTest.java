package pl.edu.agh.geotime.web.rest;

import org.springframework.security.test.context.support.WithUserDetails;
import pl.edu.agh.geotime.GeoTimeApp;

import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.repository.DepartmentRepository;
import pl.edu.agh.geotime.repository.SubdepartmentRepository;
import pl.edu.agh.geotime.service.SubdepartmentService;
import pl.edu.agh.geotime.web.rest.dto.SubdepartmentDTO;
import pl.edu.agh.geotime.web.rest.mapper.SubdepartmentMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

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

import javax.persistence.EntityManager;
import java.util.List;

import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SubdepartmentResource REST controller.
 *
 * @see SubdepartmentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class SubdepartmentResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_NAME = "BBBBBBBBBB";

    private static final String TEST_USER_LOGIN = "system";

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SubdepartmentRepository subdepartmentRepository;

    @Autowired
    private SubdepartmentMapper subdepartmentMapper;

    @Autowired
    private SubdepartmentService subdepartmentService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSubdepartmentMockMvc;

    private Subdepartment subdepartment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SubdepartmentResource subdepartmentResource = new SubdepartmentResource(subdepartmentService, subdepartmentMapper);
        this.restSubdepartmentMockMvc = MockMvcBuilders.standaloneSetup(subdepartmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Subdepartment createEntity(EntityManager em) {
        return new Subdepartment()
            .name(DEFAULT_NAME)
            .shortName(DEFAULT_SHORT_NAME);
    }

    @Before
    public void initTest() {
        subdepartment = createEntity(em);

        Department department = departmentRepository.findOne(1L);
        subdepartment.setDepartment(department);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createSubdepartment() throws Exception {
        int databaseSizeBeforeCreate = subdepartmentRepository.findAll().size();

        // Create the Subdepartment
        SubdepartmentDTO subdepartmentDTO = subdepartmentMapper.toDto(subdepartment);
        restSubdepartmentMockMvc.perform(post("/api/subdepartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subdepartmentDTO)))
            .andExpect(status().isCreated());

        // Validate the Subdepartment in the database
        List<Subdepartment> subdepartmentList = subdepartmentRepository.findAll();
        assertThat(subdepartmentList).hasSize(databaseSizeBeforeCreate + 1);
        Subdepartment testSubdepartment = subdepartmentList.get(subdepartmentList.size() - 1);
        assertThat(testSubdepartment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSubdepartment.getShortName()).isEqualTo(DEFAULT_SHORT_NAME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void createSubdepartmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = subdepartmentRepository.findAll().size();

        // Create the Subdepartment with an existing ID
        subdepartment.setId(1L);
        SubdepartmentDTO subdepartmentDTO = subdepartmentMapper.toDto(subdepartment);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubdepartmentMockMvc.perform(post("/api/subdepartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subdepartmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Subdepartment in the database
        List<Subdepartment> subdepartmentList = subdepartmentRepository.findAll();
        assertThat(subdepartmentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = subdepartmentRepository.findAll().size();
        // set the field null
        subdepartment.setName(null);

        // Create the Subdepartment, which fails.
        SubdepartmentDTO subdepartmentDTO = subdepartmentMapper.toDto(subdepartment);

        restSubdepartmentMockMvc.perform(post("/api/subdepartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subdepartmentDTO)))
            .andExpect(status().isBadRequest());

        List<Subdepartment> subdepartmentList = subdepartmentRepository.findAll();
        assertThat(subdepartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void checkShortNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = subdepartmentRepository.findAll().size();
        // set the field null
        subdepartment.setShortName(null);

        // Create the Subdepartment, which fails.
        SubdepartmentDTO subdepartmentDTO = subdepartmentMapper.toDto(subdepartment);

        restSubdepartmentMockMvc.perform(post("/api/subdepartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subdepartmentDTO)))
            .andExpect(status().isBadRequest());

        List<Subdepartment> subdepartmentList = subdepartmentRepository.findAll();
        assertThat(subdepartmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getAllSubdepartments() throws Exception {
        // Initialize the database
        subdepartmentRepository.saveAndFlush(subdepartment);

        // Get all the subdepartmentList
        restSubdepartmentMockMvc.perform(get("/api/subdepartments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subdepartment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].shortName").value(hasItem(DEFAULT_SHORT_NAME)));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getSubdepartment() throws Exception {
        // Initialize the database
        subdepartmentRepository.saveAndFlush(subdepartment);

        // Get the subdepartment
        restSubdepartmentMockMvc.perform(get("/api/subdepartments/{id}", subdepartment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(subdepartment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.shortName").value(DEFAULT_SHORT_NAME));
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void getNonExistingSubdepartment() throws Exception {
        // Get the subdepartment
        restSubdepartmentMockMvc.perform(get("/api/subdepartments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateSubdepartment() throws Exception {
        // Initialize the database
        subdepartmentRepository.saveAndFlush(subdepartment);
        int databaseSizeBeforeUpdate = subdepartmentRepository.findAll().size();

        // Update the subdepartment
        Subdepartment updatedSubdepartment = subdepartmentRepository.findOne(subdepartment.getId());
        // Disconnect from session so that the updates on updatedSubdepartment are not directly saved in db
        em.detach(updatedSubdepartment);
        updatedSubdepartment
            .name(UPDATED_NAME)
            .shortName(UPDATED_SHORT_NAME);
        SubdepartmentDTO subdepartmentDTO = subdepartmentMapper.toDto(updatedSubdepartment);

        restSubdepartmentMockMvc.perform(put("/api/subdepartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subdepartmentDTO)))
            .andExpect(status().isOk());

        // Validate the Subdepartment in the database
        List<Subdepartment> subdepartmentList = subdepartmentRepository.findAll();
        assertThat(subdepartmentList).hasSize(databaseSizeBeforeUpdate);
        Subdepartment testSubdepartment = subdepartmentList.get(subdepartmentList.size() - 1);
        assertThat(testSubdepartment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubdepartment.getShortName()).isEqualTo(UPDATED_SHORT_NAME);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void updateNonExistingSubdepartment() throws Exception {
        int databaseSizeBeforeUpdate = subdepartmentRepository.findAll().size();

        // Create the Subdepartment
        SubdepartmentDTO subdepartmentDTO = subdepartmentMapper.toDto(subdepartment);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSubdepartmentMockMvc.perform(put("/api/subdepartments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subdepartmentDTO)))
            .andExpect(status().isCreated());

        // Validate the Subdepartment in the database
        List<Subdepartment> subdepartmentList = subdepartmentRepository.findAll();
        assertThat(subdepartmentList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @WithUserDetails(TEST_USER_LOGIN)
    @Transactional
    public void deleteSubdepartment() throws Exception {
        // Initialize the database
        subdepartmentRepository.saveAndFlush(subdepartment);
        int databaseSizeBeforeDelete = subdepartmentRepository.findAll().size();

        // Get the subdepartment
        restSubdepartmentMockMvc.perform(delete("/api/subdepartments/{id}", subdepartment.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Subdepartment> subdepartmentList = subdepartmentRepository.findAll();
        assertThat(subdepartmentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subdepartment.class);
        Subdepartment subdepartment1 = new Subdepartment();
        subdepartment1.setId(1L);
        Subdepartment subdepartment2 = new Subdepartment();
        subdepartment2.setId(subdepartment1.getId());
        assertThat(subdepartment1).isEqualTo(subdepartment2);
        subdepartment2.setId(2L);
        assertThat(subdepartment1).isNotEqualTo(subdepartment2);
        subdepartment1.setId(null);
        assertThat(subdepartment1).isNotEqualTo(subdepartment2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubdepartmentDTO.class);
        SubdepartmentDTO subdepartmentDTO1 = new SubdepartmentDTO();
        subdepartmentDTO1.setId(1L);
        SubdepartmentDTO subdepartmentDTO2 = new SubdepartmentDTO();
        assertThat(subdepartmentDTO1).isNotEqualTo(subdepartmentDTO2);
        subdepartmentDTO2.setId(subdepartmentDTO1.getId());
        assertThat(subdepartmentDTO1).isEqualTo(subdepartmentDTO2);
        subdepartmentDTO2.setId(2L);
        assertThat(subdepartmentDTO1).isNotEqualTo(subdepartmentDTO2);
        subdepartmentDTO1.setId(null);
        assertThat(subdepartmentDTO1).isNotEqualTo(subdepartmentDTO2);
    }
}
