import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.*;

public class SmartAttendanceAnalyzer {

    static final double MIN_ATTENDANCE = 75.0;
    static final int SEMESTER_CLASSES = 100;
    static final String DATA_FILE = "attendance_data.csv";

    static Scanner sc = new Scanner(System.in);

    static List<Student> students = new ArrayList<>();
    static Map<String, Subject> subjects = new HashMap<>();

    /* ===================== MAIN ===================== */

    public static void main(String[] args) {

        loadData();

        int choice;
        do {
            System.out.println("\n===== SMART ATTENDANCE ANALYZER =====");
            System.out.println("1. Add Student");
            System.out.println("2. Add Subject");
            System.out.println("3. Mark Attendance");
            System.out.println("4. View Attendance Summary");
            System.out.println("5. Weekly Report");
            System.out.println("6. Monthly Report");
            System.out.println("7. Predict Attendance Recovery");
            System.out.println("8. Save & Exit");
            System.out.print("Enter choice: ");

            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                sc.nextLine();
                System.out.println("Invalid input!");
                continue;
            }

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> addSubject();
                case 3 -> markAttendance();
                case 4 -> viewSummary();
                case 5 -> generateWeeklyReport();
                case 6 -> generateMonthlyReport();
                case 7 -> predictRecovery();
                case 8 -> saveAndExit();
                default -> System.out.println("Invalid choice!");
            }
        } while (true);
    }

    /* ===================== CLASSES ===================== */

    static class Student {
        String id;
        String name;
        Map<String, List<AttendanceRecord>> attendanceMap = new HashMap<>();

        Student(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class Subject {
        String code;
        String name;
        int totalPlannedClasses = SEMESTER_CLASSES;

        Subject(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    static class AttendanceRecord {
        LocalDate date;
        boolean present;

        AttendanceRecord(LocalDate date, boolean present) {
            this.date = date;
            this.present = present;
        }
    }

    /* ===================== CORE FEATURES ===================== */

    static void addStudent() {
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine();

        for (Student s : students) {
            if (s.id.equals(id)) {
                System.out.println("Student ID already exists.");
                return;
            }
        }

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();

        students.add(new Student(id, name));
        System.out.println("Student added.");
    }

    static void addSubject() {
        System.out.print("Enter Subject Code: ");
        String code = sc.nextLine();

        if (subjects.containsKey(code)) {
            System.out.println("Subject already exists.");
            return;
        }

        System.out.print("Enter Subject Name: ");
        String name = sc.nextLine();

        subjects.put(code, new Subject(code, name));
        System.out.println("Subject added.");
    }

    static void markAttendance() {

        Student student = findStudent();
        if (student == null) return;

        Subject subject = findSubject();
        if (subject == null) return;

        List<AttendanceRecord> records =
                student.attendanceMap.computeIfAbsent(subject.code, k -> new ArrayList<>());

        for (AttendanceRecord r : records) {
            if (r.date.equals(LocalDate.now())) {
                System.out.println("Attendance already marked today.");
                return;
            }
        }

        System.out.print("Present? (P/A): ");
        char ch = sc.nextLine().toUpperCase().charAt(0);
        boolean present = ch == 'P';

        records.add(new AttendanceRecord(LocalDate.now(), present));
        System.out.println("Attendance recorded.");
    }

    static void viewSummary() {

        Student student = findStudent();
        if (student == null) return;

        for (String subjectCode : student.attendanceMap.keySet()) {
            Subject subject = subjects.get(subjectCode);
            List<AttendanceRecord> records = student.attendanceMap.get(subjectCode);

            printStats(subject, records);
        }
    }

    /* ===================== REPORTS ===================== */

    static void generateWeeklyReport() {
        generateReport(7);
    }

    static void generateMonthlyReport() {
        generateReport(30);
    }

    static void generateReport(int days) {

        Student student = findStudent();
        if (student == null) return;

        LocalDate now = LocalDate.now();

        System.out.println("\n------ REPORT (" + days + " DAYS) ------");
        for (String subjectCode : student.attendanceMap.keySet()) {

            Subject subject = subjects.get(subjectCode);
            List<AttendanceRecord> filtered = new ArrayList<>();

            for (AttendanceRecord r : student.attendanceMap.get(subjectCode)) {
                long diff = ChronoUnit.DAYS.between(r.date, now);
                if (diff <= days) filtered.add(r);
            }

            printStats(subject, filtered);
        }
    }

    /* ===================== PREDICTION ===================== */

    static void predictRecovery() {

        Student student = findStudent();
        if (student == null) return;

        Subject subject = findSubject();
        if (subject == null) return;

        List<AttendanceRecord> records =
                student.attendanceMap.getOrDefault(subject.code, new ArrayList<>());

        int conducted = records.size();
        int attended = (int) records.stream().filter(r -> r.present).count();
        int remaining = subject.totalPlannedClasses - conducted;

        double maxPossible =
                ((double) (attended + remaining) /
                        (conducted + remaining)) * 100;

        System.out.printf("Max Possible Attendance: %.2f%%\n", maxPossible);
        System.out.println(maxPossible >= MIN_ATTENDANCE ?
                "Recovery Possible" : "Recovery NOT Possible");
    }

    /* ===================== FILE HANDLING ===================== */

    static void saveAndExit() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Student s : students) {
                for (String sub : s.attendanceMap.keySet()) {
                    for (AttendanceRecord r : s.attendanceMap.get(sub)) {
                        pw.println(s.id + "," + s.name + "," + sub + "," +
                                r.date + "," + (r.present ? "P" : "A"));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
        System.out.println("Data saved. Exiting.");
        System.exit(0);
    }

    static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                Student s = getOrCreateStudent(p[0], p[1]);
                s.attendanceMap
                        .computeIfAbsent(p[2], k -> new ArrayList<>())
                        .add(new AttendanceRecord(LocalDate.parse(p[3]), p[4].equals("P")));
            }
        } catch (Exception e) {
            System.out.println("Error loading data.");
        }
    }

    /* ===================== HELPERS ===================== */

    static Student getOrCreateStudent(String id, String name) {
        for (Student s : students)
            if (s.id.equals(id)) return s;

        Student s = new Student(id, name);
        students.add(s);
        return s;
    }

    static Student findStudent() {
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine();

        for (Student s : students)
            if (s.id.equals(id)) return s;

        System.out.println("Student not found.");
        return null;
    }

    static Subject findSubject() {
        System.out.print("Enter Subject Code: ");
        String code = sc.nextLine();

        if (!subjects.containsKey(code)) {
            System.out.println("Subject not found.");
            return null;
        }
        return subjects.get(code);
    }
    static void printStats(Subject subject, List<AttendanceRecord> records) {
        int conducted = records.size();
        int attended = (int) records.stream().filter(r -> r.present).count();
        int missed = conducted - attended;
        double attendance = conducted == 0 ? 0 : (attended * 100.0 / conducted);
        double bunk = conducted == 0 ? 0 : (missed * 100.0 / conducted);
        System.out.println("\nSubject: " + subject.name);
        System.out.println("Conducted: " + conducted);
        System.out.println("Attended: " + attended);
        System.out.printf("Attendance: %.2f%%\n", attendance);
        System.out.printf("Bunk: %.2f%%\n", bunk);
        System.out.println(attendance < MIN_ATTENDANCE ? "Status: AT RISK" : "Status: SAFE");
    }
}