package pl.edu.agh.geotime.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.GeoTimeApp;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.repository.SemesterRepository;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pl.edu.agh.geotime.web.rest.TestUtil.createFormattingConversionService;
import static pl.edu.agh.geotime.web.rest.TestUtil.sameInstant;

/**
 * Test class for the SemesterInfo REST controller.
 *
 * @see SemesterInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeoTimeApp.class)
public class SemesterInfoResourceIntTest {

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSemesterInfoMockMvc;

    private Semester semester;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SemesterInfoResource semesterInfoResource = new SemesterInfoResource(semesterService);
        this.restSemesterInfoMockMvc = MockMvcBuilders.standaloneSetup(semesterInfoResource)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Before
    public void initTest() {
        semester = SemesterResourceIntTest.createEntity(em);
        semester.setStartDate(ZonedDateTime.now().minusDays(10));
        semester.setEndDate(ZonedDateTime.now().plusDays(20));
        semester.setActive(true);
    }

    @Test
    @Transactional
    public void getCurrentSemesterInfo() throws Exception {
        Semester currentSemester = semesterService.getCurrentSemester()
            .orElse(semesterRepository.save(semester));

        // Get all the departmentsInfoList
        restSemesterInfoMockMvc.perform(get("/api/semester-info/current"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.start").value(sameInstant(currentSemester.getStartDate())))
            .andExpect(jsonPath("$.end").value(sameInstant(currentSemester.getEndDate())));
    }
}
