# 📋 Smart Attendance Analyzer

A Java console application for tracking and analyzing student attendance across subjects — with report generation, recovery prediction, and CSV-based persistent storage.

---

## ✨ Features

- **Student & Subject Management** — Add and manage multiple students and subjects
- **Daily Attendance Marking** — Mark present/absent per student per subject (once per day)
- **Attendance Summary** — View conducted, attended, missed classes and percentage
- **Weekly & Monthly Reports** — Filter attendance records by date range
- **Recovery Prediction** — Predicts whether a student can recover to 75% attendance
- **Persistent Storage** — Saves and loads data from a local CSV file

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java | Core language |
| Java Collections Framework | `List`, `Map`, `ArrayList`, `HashMap` |
| `java.time.LocalDate` | Date-based attendance tracking |
| `java.time.temporal.ChronoUnit` | Date range calculations for reports |
| File I/O (`BufferedReader`, `PrintWriter`) | CSV data persistence |

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 17 or higher

### Run the project

```bash
# Compile
javac Attendance.java

# Run
java SmartAttendanceAnalyzer
```

---

## 📂 Project Structure

```
├── Attendance.java          # Main source file
├── attendance_data.csv      # Auto-generated data file (after first save)
└── README.md
```

---

## 🖥️ Menu Options

```
===== SMART ATTENDANCE ANALYZER =====
1. Add Student
2. Add Subject
3. Mark Attendance
4. View Attendance Summary
5. Weekly Report
6. Monthly Report
7. Predict Attendance Recovery
8. Save & Exit
```

---

## 📊 Attendance Logic

| Metric | Formula |
|---|---|
| Attendance % | `(Attended / Conducted) × 100` |
| Bunk % | `(Missed / Conducted) × 100` |
| Max Possible % | `(Attended + Remaining) / (Conducted + Remaining) × 100` |
| Minimum Required | **75%** |

> A student is marked **AT RISK** if their attendance falls below 75%.

---

## 💾 Data Persistence

Attendance data is saved to `attendance_data.csv` on exit and automatically loaded on the next run.

**CSV Format:**
```
StudentID,StudentName,SubjectCode,Date,P/A
```

**Example:**
```
1CD24CS025,Yash,CS101,2024-11-15,P
1CD24CS025,Yash,CS101,2024-11-16,A
```

---

## 📌 Constants

| Constant | Value |
|---|---|
| `MIN_ATTENDANCE` | 75.0% |
| `SEMESTER_CLASSES` | 100 |
| `DATA_FILE` | `attendance_data.csv` |

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
