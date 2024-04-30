package com.example.studentSystem.services;

import com.example.studentSystem.models.Student;
import com.example.studentSystem.models.User;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {
    // Ссылка на базу данных PostgreSQL
    private final String url = "jdbc:postgresql://[::1]:5432/postgres";
    private final String username = "postgres";
    private final String password = "---";
    private final List<Student> studentList = new ArrayList<>();
    private final List<User> teacherList = new ArrayList<>();
    // Конекшены к бдшкам
    Connection connToStudents;
    Connection connToMarks;
    Connection connToSubjects;
    Connection connToUsers;

    // Строки для SQL-запросов
    String sqlInsert = "INSERT INTO student_table(name, surname, age, city, direction) VALUES(?,?,?,?,?)";
    String sqlInsertUser = "INSERT INTO Users(UserID, StudentID, UserLogin, UserPasswordSHA256, UserRole) VALUES (?, ?, ?, ?, ?)";
    String sqlDelete = "DELETE FROM student_table WHERE id = ?";
    String sqlCreate;
    // Список предметов
    Map<String, String[]> subjects = new HashMap<>();
    {
        try {
            connToStudents = DriverManager.getConnection(url, username, password);
            connToMarks = DriverManager.getConnection(url, username, password);
            connToSubjects = DriverManager.getConnection(url, username, password);

            // Получение списка предметов
            Statement stmtSubjects = connToSubjects.createStatement();
            ResultSet rsSubjects = stmtSubjects.executeQuery("SELECT DISTINCT direction_name FROM subject_table");

            while (rsSubjects.next()) {
                String directionName = rsSubjects.getString("direction_name");
                Statement stmtDirection = connToSubjects.createStatement();
                ResultSet rsDirection = stmtDirection.executeQuery("SELECT subject_name FROM subject_table WHERE direction_name='" + directionName + "'");
                List<String> subjectList = new ArrayList<>();
                while (rsDirection.next()) {
                    subjectList.add(rsDirection.getString("subject_name"));
                }
                subjects.put(directionName, subjectList.toArray(new String[0]));
            }

            // Получение списка студентов
            Statement stmtStudents = connToStudents.createStatement();
            ResultSet rsStudents = stmtStudents.executeQuery("SELECT * FROM student_table");

            while (rsStudents.next()) {
                int id = rsStudents.getInt("id");
                List<String> dateList = new ArrayList<>();
                List<String> subjectList = new ArrayList<>();
                List<Integer> markList = new ArrayList<>();
                Statement stmtMarks = connToMarks.createStatement();
                ResultSet rsMarks = stmtMarks.executeQuery("SELECT * FROM student" + id);
                while (rsMarks.next()) {
                    dateList.add(rsMarks.getString("Date"));
                    subjectList.add(rsMarks.getString("Subject"));
                    markList.add(rsMarks.getInt("Mark"));
                }
                studentList.add(new Student(id, rsStudents.getString("name"), rsStudents.getString("surname"),
                        rsStudents.getInt("age"), rsStudents.getString("city"), rsStudents.getString("direction"),
                        dateList, subjectList, markList));
            }

            // Получение списка учителей
            Statement stmtTeachers = connToStudents.createStatement();
            ResultSet rsTeachers = stmtStudents.executeQuery("SELECT * FROM Users;");
            while(rsTeachers.next()) {
                if(rsTeachers.getString("UserRole").equals("teacher")) {
                    User temp_user = new User(rsTeachers.getString("UserLogin"),
                            rsTeachers.getString("UserPasswordSHA256"),
                            rsTeachers.getString("UserRole"));
                    temp_user.setStudentID(0);
                    teacherList.add(temp_user);
                }
            }

            stmtTeachers.close();
            stmtStudents.close();
            stmtSubjects.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> listStudents() {
        return studentList;
    }

    public Set<String> listDirections() {
        return subjects.keySet();
    }

    public List<User> listTeacher() {
        return teacherList;
    }
    public Student getStudentById(int id) {
        for (Student student : studentList) {
            if (student.getId() == id) return student;
        }
        return null;
    }

    public void saveStudent(Student student) {
        try {
            student.dateList = new ArrayList<>();
            student.subjectList = new ArrayList<>();
            student.markList = new ArrayList<>();
            studentList.add(student);

            PreparedStatement pstmt = connToStudents.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getSurname());
            pstmt.setInt(3, student.getAge());
            pstmt.setString(4, student.getCity());
            pstmt.setString(5, student.getEducationalDirection());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                student.setId(id);
            }

            pstmt.close();

            sqlCreate = "CREATE TABLE IF NOT EXISTS student" + student.getId() + " (Date VARCHAR(10), Subject VARCHAR(255), Mark INTEGER);";

            Statement stmt = connToMarks.createStatement();
            stmt.execute(sqlCreate);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTeacher(User user) {

        try {
            Statement stmtTeachers = connToUsers.createStatement();
            ResultSet rsTeachers = stmtTeachers.executeQuery("SELECT COUNT(*) as user_count FROM Users;");
            rsTeachers.next();

            User user_teacher = new User(user.getUserLogin(), Hashing.sha256()
				.hashString(user.getUserPasswordSHA256(), StandardCharsets.UTF_8)
				.toString(), user.getUserRole());

            user.setUserPasswordSHA256(Hashing.sha256()
                    .hashString(user.getUserPasswordSHA256(), StandardCharsets.UTF_8)
                    .toString());
            teacherList.add(user_teacher);

            PreparedStatement pstmt = connToUsers.prepareStatement(sqlInsertUser, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, rsTeachers.getInt("user_count") + 1);
            pstmt.setInt(2, user.getStudentID());
            pstmt.setString(3, user.getUserLogin());
            pstmt.setString(4, user.getUserPasswordSHA256());
            pstmt.setString(5, user.getUserRole());
            pstmt.executeUpdate();
            stmtTeachers.close();

            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStudent(int id) {
        studentList.removeIf(student -> student.getId() == id);

        try {
            PreparedStatement pstmt = connToStudents.prepareStatement(sqlDelete);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();

            Statement stmt = connToMarks.createStatement();
            stmt.execute("DROP TABLE IF EXISTS student" + id + ";");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserValid(User user) {
        boolean status = false;

        // SHA-256 encryption
        user.setUserPasswordSHA256(Hashing.sha256()
                .hashString(user.getUserPasswordSHA256(), StandardCharsets.UTF_8)
                .toString());

        try {
            connToUsers = DriverManager.getConnection(url, username, password);
            Statement stmtUsers = connToUsers.createStatement();
            ResultSet rsUsers = stmtUsers.executeQuery("SELECT * FROM Users");
            while (rsUsers.next()) {
                if (rsUsers.getString("UserLogin").equals(user.getUserLogin()) &&
                        rsUsers.getString("UserPasswordSHA256").equals(user.getUserPasswordSHA256())) {
                    // Дополнительно устанавливаем роль и студента для отображения
                    user.setUserRole(rsUsers.getString("UserRole"));
                    user.setStudentID(rsUsers.getInt("StudentID"));
                    // Успешный вход = true
                    status = true;
                    break;
                }
            }

            stmtUsers.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return status;
    }

    public void insertMark(String date, String subject, int mark, int id) {
        try {
            Student student = getStudentById(id);
            student.getMarkList().add(mark);
            student.getDateList().add(date);
            student.getSubjectList().add(subject);
            studentList.set(studentList.indexOf(student), student);

            String sqlInsert = "INSERT INTO student" + id + " (Date, Subject, Mark) VALUES (?,?,?);";

            PreparedStatement stmt = connToMarks.prepareStatement(sqlInsert);
            stmt.setString(1, date);
            stmt.setString(2, subject);
            stmt.setInt(3, mark);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String[] getSubjectsByDirection(String educationDirection) {
        return subjects.get(educationDirection);
    }
}
