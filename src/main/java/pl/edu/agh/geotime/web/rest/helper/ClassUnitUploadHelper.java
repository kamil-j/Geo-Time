package pl.edu.agh.geotime.web.rest.helper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;
import pl.edu.agh.geotime.domain.enumeration.ClassFrequency;
import pl.edu.agh.geotime.service.*;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class ClassUnitUploadHelper {

    private static final String NOT_EXISTS = "notexists";

    private final ClassUnitService classUnitService;
    private final ClassTypeService classTypeService;
    private final SubdepartmentService subdepartmentService;
    private final SemesterService semesterService;
    private final StudyFieldService studyFieldService;
    private final AcademicUnitService academicUnitService;
    private final RoomService roomService;
    private final ClassUnitGroupService classUnitGroupService;

    @Autowired
    public ClassUnitUploadHelper(ClassUnitService classUnitService, ClassTypeService classTypeService,
                                 SubdepartmentService subdepartmentService, SemesterService semesterService,
                                 StudyFieldService studyFieldService, AcademicUnitService academicUnitService,
                                 RoomService roomService, ClassUnitGroupService classUnitGroupService) {
        this.classUnitService = classUnitService;
        this.classTypeService = classTypeService;
        this.subdepartmentService = subdepartmentService;
        this.semesterService = semesterService;
        this.studyFieldService = studyFieldService;
        this.academicUnitService = academicUnitService;
        this.roomService = roomService;
        this.classUnitGroupService = classUnitGroupService;
    }

    public void uploadFromFile(MultipartFile file, Long subdepartmentId) throws IOException {
        List<ClassUnit> classUnitsToCreate = new ArrayList<>();

        Subdepartment subdepartment = getSubdepartmentById(subdepartmentId);
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if(row.getRowNum() == 0) { //Skip file header
                continue;
            }
            if(row.getCell(0).getStringCellValue().isEmpty()) { //Break if no data
                break;
            }
            classUnitsToCreate.add(processFileRow(row, subdepartment));
        }
        classUnitsToCreate.forEach(classUnitService::save);
    }

    private Subdepartment getSubdepartmentById(Long id) {
        return subdepartmentService.findById(id).orElseThrow(() ->
            new BadRequestAlertException("Cannot find subdepartment with id: " + id,
                "userExt", NOT_EXISTS)
        );
    }

    private ClassUnit processFileRow(Row row, Subdepartment subdepartment) {
        String title = row.getCell(0).getStringCellValue();
        int academicUnitYearNumber = (int) row.getCell(1).getNumericCellValue();
        long semesterId = (long) row.getCell(2).getNumericCellValue();
        String studyFieldShortName = row.getCell(3).getStringCellValue();
        int academicUnitDegreeNumber = (int) row.getCell(4).getNumericCellValue();

        int academicUnitGroupNumber = -1;
        if(row.getCell(5) != null) {
            academicUnitGroupNumber =  (int) row.getCell(5).getNumericCellValue();
        }

        String classTypeShortName = row.getCell(6).getStringCellValue();
        int hoursQuantity = (int) row.getCell(7).getNumericCellValue();
        int duration = (int) row.getCell(8).getNumericCellValue();
        String frequencyShortName = row.getCell(9).getStringCellValue();

        DataFormatter df = new DataFormatter();
        String[] roomNames = df.formatCellValue(row.getCell(10)).split(",");

        Long classUnitGroupId = null;
        if(row.getCell(11) != null) {
            classUnitGroupId =  (long) row.getCell(11).getNumericCellValue();
        }

        ClassUnit classUnit = new ClassUnit();
        classUnit.setTitle(title);
        classUnit.setDuration(duration);
        classUnit.setHoursQuantity(hoursQuantity);
        classUnit.setFrequency(ClassFrequency.fromShortName(frequencyShortName));
        if(academicUnitGroupNumber != -1) {
            classUnit.setAcademicUnitGroup(AcademicUnitGroup.fromGroupNumber(academicUnitGroupNumber));
        }
        classUnit.setClassType(getClassTypeByShortName(classTypeShortName));
        classUnit.setSubdepartment(subdepartment);
        classUnit.setSemester(getSemesterById(semesterId));
        classUnit.setAcademicUnit(getAcademicUnitByStudyFieldShortNameAndYearAndDegree(studyFieldShortName,
            academicUnitYearNumber, academicUnitDegreeNumber));
        if(classUnitGroupId != null) {
            classUnit.setClassUnitGroup(getClassUnitGroupById(classUnitGroupId));
        }
        classUnit.setRooms(getRoomsByRoomNames(roomNames));

        return classUnit;
    }

    private ClassUnitGroup getClassUnitGroupById(Long classUnitGroupId) {
        return classUnitGroupService.findById(classUnitGroupId)
                    .orElseThrow(() -> new BadRequestAlertException("Cannot find classUnitGroup with id: "
                    + classUnitGroupId, "classUnitGroup", NOT_EXISTS)
                );
    }

    private AcademicUnit getAcademicUnitByStudyFieldShortNameAndYearAndDegree(String studyFieldShortName,
                                                                            int academicUnitYearNumber,
                                                                            int academicUnitDegreeNumber) {
        StudyField studyField = studyFieldService.findByShortName(studyFieldShortName)
            .orElseThrow(() -> new BadRequestAlertException("Cannot find study field with studyFieldShortName: "
                + studyFieldShortName, "studyField", NOT_EXISTS)
            );

        return academicUnitService.findByStudyFieldIdAndYearAndDegree(studyField.getId(),
            AcademicUnitYear.fromYearNumber(academicUnitYearNumber),
            AcademicUnitDegree.fromDegreeNumber(academicUnitDegreeNumber)
        ).orElseThrow(() -> new BadRequestAlertException("Cannot find academicUnit with studyFieldId: "
            + studyField.getId() + " and year: " + academicUnitYearNumber + " and degree: " + academicUnitDegreeNumber,
            "academicUnit", NOT_EXISTS)
        );
    }

    private ClassType getClassTypeByShortName(String shortName) {
        return classTypeService.findByShortName(shortName).orElseThrow(() ->
            new BadRequestAlertException("Cannot find class type with shortName: " + shortName,
                "classType", NOT_EXISTS)
        );
    }

    private Semester getSemesterById(Long semesterId) {
        return semesterService.findById(semesterId).orElseThrow(() ->
            new BadRequestAlertException("Cannot find semester with id: " + semesterId,
                "semester", NOT_EXISTS)
        );
    }

    private Set<Room> getRoomsByRoomNames(String[] roomNames) {
        Set<Room> rooms = new HashSet<>();
        Stream.of(roomNames).forEach(roomName -> {
            if(roomName.trim().isEmpty()) {
                return;
            }
            Room room = roomService.findByName(roomName).orElseThrow(() ->
                new BadRequestAlertException("Cannot find room with name: " + roomName, "room", NOT_EXISTS)
            );
            rooms.add(room);
        });
        return rooms;
    }
}
