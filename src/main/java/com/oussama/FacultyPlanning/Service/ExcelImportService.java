package com.oussama.FacultyPlanning.Service;

import com.oussama.FacultyPlanning.Enum.LVL;
import com.oussama.FacultyPlanning.Enum.Role;
import com.oussama.FacultyPlanning.Enum.RoomType;
import com.oussama.FacultyPlanning.Model.*;
import com.oussama.FacultyPlanning.Repository.*;
import com.oussama.FacultyPlanning.Utility.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ExcelImportService {
    private final RoomRepository roomRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final SectionRepository sectionRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final TimeslotRepository timeslotRepository;

    public void importCourses(MultipartFile file, Long facultyId) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Optional<Semester> semesterOptional = semesterRepository.findCurrentSemesterByFacultyId(facultyId);
        if (semesterOptional.isEmpty()) throw new RuntimeException("no current semester found");
        Semester semester = semesterOptional.get();
        String[] colors = {"bg-yellow-300", "bg-blue-300", "bg-green-300", "bg-purple-300", "bg-red-300", "bg-amber-300", "bg-blue-400", "bg-green-400", "bg-emerald-300", "bg-orange-300"};
        Random random = new Random();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            String subjectName = getStringCellValue(row.getCell(0));
            String teacherName = getStringCellValue(row.getCell(1));
            String groupsStr = getStringCellValue(row.getCell(2));
            String courseTypeStr = getStringCellValue(row.getCell(3));

            // Optional columns - handle gracefully if empty or null
            String timeslotIdStr = getOptionalStringCellValue(row.getCell(4));
            String roomCode = getOptionalStringCellValue(row.getCell(5));

            System.out.println("i am here");
            Subject subject = subjectRepository.findSubjectByTitleAndFacultyId(subjectName, facultyId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectName));
            System.out.println(subject.getId());

            User teacher = userRepository.findByFullNameAndFacultyId(teacherName, facultyId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found: " + teacherName));

            System.out.println(teacher.getId());

            Set<Group> groups = new HashSet<>();
            if (!groupsStr.isEmpty()) {
                String[] groupEntries = groupsStr.split(",");
                for (String entry : groupEntries) {
                    String[] parts = entry.trim().split("-");
                    if (parts.length != 2) {
                        throw new RuntimeException("Invalid group format: " + entry);
                    }
                    String groupCode = parts[0].trim();
                    String sectionName = parts[1].trim();

                    Section section = sectionRepository.findSectionByFacultyIdAndName(facultyId, sectionName)
                            .orElseThrow();

                    Group group = groupRepository.findByCodeAndSection(groupCode, section)
                            .orElseThrow();

                    groups.add(group);
                }
            }

            RoomType roomType = RoomType.valueOf(courseTypeStr.trim().toUpperCase());

            // Handle optional timeslot
            Timeslot timeslot = null;
            if (timeslotIdStr != null && !timeslotIdStr.trim().isEmpty()) {
                try {
                    Long timeslotId = Long.parseLong(timeslotIdStr.trim());
                    timeslot = timeslotRepository.findById(timeslotId).orElse(null);
                    if (timeslot == null) {
                        System.out.println("Warning: Timeslot with ID " + timeslotId + " not found");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid timeslot ID format: " + timeslotIdStr);
                }
            }

            // Handle optional room
            Room room = null;
            if (roomCode != null && !roomCode.trim().isEmpty()) {
                room = (Room) roomRepository.findByCodeAndFacultyId(roomCode.trim(), facultyId).orElse(null);
                if (room == null) {
                    System.out.println("Warning: Room with code " + roomCode + " not found in faculty " + facultyId);
                }
            }

            String color = colors[random.nextInt(colors.length)];

            Course course = Course.builder()
                    .subject(subject)
                    .teacher(teacher)
                    .type(roomType)
                    .groups(groups)
                    .color(color)
                    .semester(semester)
                    .timeslot(timeslot)  // Will be null if not provided or not found
                    .room(room)          // Will be null if not provided or not found
                    .build();

            courseRepository.save(course);
        }
        workbook.close();
    }

    // Helper method to safely get string cell values for optional columns
    private String getOptionalStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> {
                String value = cell.getStringCellValue();
                yield (value == null || value.trim().isEmpty()) ? null : value.trim();
            }
            case NUMERIC ->
                // Handle numeric values (like IDs) as strings
                    String.valueOf((long) cell.getNumericCellValue());
            case BLANK -> null;
            default -> null;
        };
    }

    public List<User> importTeachersFromExcel(MultipartFile file, Long facultyId) throws IOException {
        List<User> importedTeachers = new ArrayList<>();
        List<String> teacherEmails = new ArrayList<>();
        List<String> teacherPasswords = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                try {
                    User teacher = new User();

                    teacher.setFirstName(getStringCellValue(row.getCell(0)));
                    teacher.setLastName(getStringCellValue(row.getCell(1)));
                    String email = getStringCellValue(row.getCell(2));
                    teacher.setEmail(email);
                    teacher.setPhone(getStringCellValue(row.getCell(3)));

                    String rawPassword = generateRandomPassword();
                    teacher.setPassword(passwordEncoder.encode(rawPassword));

                    teacher.setRole(Role.TEACHER);
                    teacher.setEnabled(true);

                    Faculty faculty = facultyRepository.findById(facultyId)
                            .orElseThrow(() -> new RuntimeException("Faculty not found"));
                    teacher.setFaculty(faculty);

                    User savedTeacher = userRepository.save(teacher);
                    importedTeachers.add(savedTeacher);

                    teacherEmails.add(email);
                    teacherPasswords.add(rawPassword);

                    // Send email asynchronously
                    //emailService.sendCredentials(teacher.getEmail(), rawPassword);

                } catch (Exception e) {
                    System.err.println("Error importing teacher at row {"+row.getRowNum()+"}: {"+e.getMessage()+"}");
                }
            }
        }
        if (!teacherEmails.isEmpty()) {
            emailService.sendBatchTeacherCredentials(teacherEmails, teacherPasswords);
        }
        return importedTeachers;
    }

    public List<Room> importRoomsFromExcel(MultipartFile file, Long facultyId) throws IOException {
        List<Room> importedRooms = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get first sheet

        int batchSize = 100;
        List<Room> batch = new ArrayList<>(batchSize);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            Room room = parseRoomRow(row, facultyId);
            if (room != null) {
                batch.add(room);
                if (batch.size() >= batchSize) {
                    roomRepository.saveAll(batch);
                    importedRooms.addAll(batch);
                    batch.clear();
                }
            }
        }
        // Save remaining records
        if (!batch.isEmpty()) {
            roomRepository.saveAll(batch);
            importedRooms.addAll(batch);
        }

        workbook.close();
        return importedRooms;
    }

    private Room parseRoomRow(Row row, Long facultyId) {
        try {
            if (row.getPhysicalNumberOfCells() < 2) {
                throw new IllegalArgumentException("Invalid row format");
            }
            Room room = new Room();
            room.setCode(getStringCellValue(row.getCell(0)));
            String roomTypeStr = getStringCellValue(row.getCell(1));
            room.setType(RoomType.valueOf(roomTypeStr.toUpperCase()));
            Faculty faculty = facultyRepository.findById(facultyId)
                    .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));
            room.setFaculty(faculty);
            if (!EnumUtils.isValidEnum(RoomType.class, roomTypeStr)) {
                throw new IllegalArgumentException("Invalid room type: " + roomTypeStr);
            }
            return room;
        } catch (Exception e) {
            // Log error and skip this row
            System.err.println("Error parsing row " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }

    public List<Subject> importSubjectsFromExcel(MultipartFile file, Long facultyId) throws IOException {
        List<Subject> importedSubjects = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get first sheet

        int batchSize = 100;
        List<Subject> batch = new ArrayList<>(batchSize);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            Subject subject = parseSubjectRow(row, facultyId);
            if (subject != null) {
                batch.add(subject);
                if (batch.size() >= batchSize) {
                    subjectRepository.saveAll(batch);
                    importedSubjects.addAll(batch);
                    batch.clear();
                }
            }
        }
        // Save remaining records
        if (!batch.isEmpty()) {
            subjectRepository.saveAll(batch);
            importedSubjects.addAll(batch);
        }

        workbook.close();
        return importedSubjects;
    }

    private Subject parseSubjectRow(Row row, Long facultyId) {
        try {
            if (row.getPhysicalNumberOfCells() < 2) {
                throw new IllegalArgumentException("Invalid row format");
            }
            Subject subject = new Subject();
            subject.setTitle(getStringCellValue(row.getCell(0)));
            subject.setCode(getStringCellValue(row.getCell(1)));
            Faculty faculty = facultyRepository.findById(facultyId)
                    .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));
            subject.setFaculty(faculty);
            return subject;
        } catch (Exception e) {
            // Log error and skip this row
            System.err.println("Error parsing row " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }

    public List<Department> importDepartmentsFromExcel(MultipartFile file, Long facultyId) throws IOException {
        List<Department> importedDepartments = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get first sheet

        int batchSize = 100;
        List<Department> batch = new ArrayList<>(batchSize);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            Department department = parseDepartmentRow(row, facultyId);
            if (department != null) {
                batch.add(department);
                if (batch.size() >= batchSize) {
                    departmentRepository.saveAll(batch);
                    importedDepartments.addAll(batch);
                    batch.clear();
                }
            }
        }
        // Save remaining records
        if (!batch.isEmpty()) {
            departmentRepository.saveAll(batch);
            importedDepartments.addAll(batch);
        }

        workbook.close();
        return importedDepartments;
    }

    private Department parseDepartmentRow(Row row, Long facultyId) {
        try {
            Department department = new Department();
            department.setName(getStringCellValue(row.getCell(0)));
            Faculty faculty = facultyRepository.findById(facultyId)
                    .orElseThrow(() -> new RuntimeException("Faculty not found with id: " + facultyId));
            department.setFaculty(faculty);
            return department;
        } catch (Exception e) {
            // Log error and skip this row
            System.err.println("Error parsing row " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }

    public List<Section> importSectionsFromExcel(MultipartFile file, Long facultyId) throws IOException {
        List<Section> importedSections = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get first sheet

        int batchSize = 100;
        List<Section> batch = new ArrayList<>(batchSize);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            Section section = parseSectionRow(row, facultyId);
            if (section != null) {
                batch.add(section);
                if (batch.size() >= batchSize) {
                    sectionRepository.saveAll(batch);
                    importedSections.addAll(batch);
                    batch.clear();
                }
            }
        }
        // Save remaining records
        if (!batch.isEmpty()) {
            sectionRepository.saveAll(batch);
            importedSections.addAll(batch);
        }

        workbook.close();
        return importedSections;
    }

    private Section parseSectionRow(Row row, Long facultyId) {
        System.out.println(facultyId);
        try {
            Section section = new Section();
            section.setName(getStringCellValue(row.getCell(0)));
            section.setLevel(LVL.valueOf(getStringCellValue(row.getCell(1)).toUpperCase()));
            Department department = departmentRepository.findDepartmentByFacultyIdAndName(facultyId, getStringCellValue(row.getCell(2)))
                    .orElseThrow(() -> new RuntimeException("Department not found with faculty id: " + facultyId));
            System.out.println(department.getName());
            section.setDepartment(department);
            return section;
        } catch (Exception e) {
            // Log error and skip this row
            System.err.println("Error parsing row " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }

    public List<Group> importGroupsFromExcel(MultipartFile file, Long facultyId) throws IOException {
        List<Group> importedGroups = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Get first sheet

        int batchSize = 100;
        List<Group> batch = new ArrayList<>(batchSize);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header
            Group group = parseGroupRow(row, facultyId);
            if (group != null) {
                batch.add(group);
                if (batch.size() >= batchSize) {
                    groupRepository.saveAll(batch);
                    importedGroups.addAll(batch);
                    batch.clear();
                }
            }
        }
        // Save remaining records
        if (!batch.isEmpty()) {
            groupRepository.saveAll(batch);
            importedGroups.addAll(batch);
        }

        workbook.close();
        return importedGroups;
    }

    private Group parseGroupRow(Row row, Long facultyId) {
        try {
            Group group = new Group();
            group.setCode(getStringCellValue(row.getCell(0)));
            Section section = sectionRepository.findSectionByFacultyIdAndName(facultyId, getStringCellValue(row.getCell(1)))
                    .orElseThrow(() -> new RuntimeException("Group not found with faculty id: " + facultyId));
            group.setSection(section);
            return group;
        } catch (Exception e) {
            // Log error and skip this row
            System.err.println("Error parsing row " + row.getRowNum() + ": " + e.getMessage());
            return null;
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> null;
        };
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (chars.length() * Math.random());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
