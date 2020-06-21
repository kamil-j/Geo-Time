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
import pl.edu.agh.geotime.domain.RoomType;
import pl.edu.agh.geotime.repository.RoomTypeRepository;
import pl.edu.agh.geotime.service.RoomTypeService;
import pl.edu.agh.geotime.web.rest.mapper.RoomTypeMapper;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the RoomTypeResource REST controller.
 *
 * @see RoomTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class RoomTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRoomTypeMockMvc;

    private RoomType roomType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RoomTypeResource roomTypeResource = new RoomTypeResource(roomTypeService, roomTypeMapper);
        this.restRoomTypeMockMvc = MockMvcBuilders.standaloneSetup(roomTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static RoomType createEntity(EntityManager em) {
        return new RoomType()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
    }

    @Before
    public void initTest() {
        roomType = createEntity(em);
    }

    @Test
    @Transactional
    public void createRoomType() throws Exception {
        int databaseSizeBeforeCreate = roomTypeRepository.findAll().size();

        // Create the RoomType
        restRoomTypeMockMvc.perform(post("/api/room-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(roomTypeMapper.toDto(roomType))))
            .andExpect(status().isCreated());

        // Validate the RoomType in the database
        List<RoomType> roomTypeList = roomTypeRepository.findAll();
        assertThat(roomTypeList).hasSize(databaseSizeBeforeCreate + 1);
        RoomType testRoomType = roomTypeList.get(roomTypeList.size() - 1);
        assertThat(testRoomType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRoomType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createRoomTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = roomTypeRepository.findAll().size();

        // Create the RoomType with an existing ID
        roomType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoomTypeMockMvc.perform(post("/api/room-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(roomTypeMapper.toDto(roomType))))
            .andExpect(status().isBadRequest());

        // Validate the RoomType in the database
        List<RoomType> roomTypeList = roomTypeRepository.findAll();
        assertThat(roomTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = roomTypeRepository.findAll().size();
        // set the field null
        roomType.setName(null);

        // Create the RoomType, which fails.

        restRoomTypeMockMvc.perform(post("/api/room-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(roomTypeMapper.toDto(roomType))))
            .andExpect(status().isBadRequest());

        List<RoomType> roomTypeList = roomTypeRepository.findAll();
        assertThat(roomTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRoomTypes() throws Exception {
        // Initialize the database
        roomTypeRepository.saveAndFlush(roomType);

        // Get all the roomTypeList
        restRoomTypeMockMvc.perform(get("/api/room-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(roomType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void getRoomType() throws Exception {
        // Initialize the database
        roomTypeRepository.saveAndFlush(roomType);

        // Get the roomType
        restRoomTypeMockMvc.perform(get("/api/room-types/{id}", roomType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(roomType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    public void getNonExistingRoomType() throws Exception {
        // Get the roomType
        restRoomTypeMockMvc.perform(get("/api/room-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRoomType() throws Exception {
        // Initialize the database
        roomTypeRepository.saveAndFlush(roomType);
        int databaseSizeBeforeUpdate = roomTypeRepository.findAll().size();

        // Update the roomType
        RoomType updatedRoomType = roomTypeRepository.findOne(roomType.getId());
        // Disconnect from session so that the updates on updatedRoomType are not directly saved in db
        em.detach(updatedRoomType);
        updatedRoomType
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);

        restRoomTypeMockMvc.perform(put("/api/room-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(roomTypeMapper.toDto(updatedRoomType))))
            .andExpect(status().isOk());

        // Validate the RoomType in the database
        List<RoomType> roomTypeList = roomTypeRepository.findAll();
        assertThat(roomTypeList).hasSize(databaseSizeBeforeUpdate);
        RoomType testRoomType = roomTypeList.get(roomTypeList.size() - 1);
        assertThat(testRoomType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRoomType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingRoomType() throws Exception {
        int databaseSizeBeforeUpdate = roomTypeRepository.findAll().size();

        // Create the RoomType

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRoomTypeMockMvc.perform(put("/api/room-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(roomTypeMapper.toDto(roomType))))
            .andExpect(status().isCreated());

        // Validate the RoomType in the database
        List<RoomType> roomTypeList = roomTypeRepository.findAll();
        assertThat(roomTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteRoomType() throws Exception {
        // Initialize the database
        roomTypeRepository.saveAndFlush(roomType);
        int databaseSizeBeforeDelete = roomTypeRepository.findAll().size();

        // Get the roomType
        restRoomTypeMockMvc.perform(delete("/api/room-types/{id}", roomType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<RoomType> roomTypeList = roomTypeRepository.findAll();
        assertThat(roomTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoomType.class);
        RoomType roomType1 = new RoomType();
        roomType1.setId(1L);
        RoomType roomType2 = new RoomType();
        roomType2.setId(roomType1.getId());
        assertThat(roomType1).isEqualTo(roomType2);
        roomType2.setId(2L);
        assertThat(roomType1).isNotEqualTo(roomType2);
        roomType1.setId(null);
        assertThat(roomType1).isNotEqualTo(roomType2);
    }
}
