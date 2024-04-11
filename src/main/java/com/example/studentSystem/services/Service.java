package com.example.studentSystem.services;

import com.example.studentSystem.models.Student;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.*;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {
    @PersistenceContext
    private EntityManager entityManager;


    // Конекшены к бдшкам
    Connection connToStudents;
    Connection connToMarks;
    Connection connToSubjects;

    // Ссылка на базу данных PostgreSQL
    String url = "jdbc:postgresql://[::1]:5432/postgres";
    String username = "postgres";
    String password = "---";

    // Строки для SQL-запросов
    String sqlInsert = "INSERT INTO student_table(name, surname, age, city, direction) VALUES(?,?,?,?,?)";
    String sqlDelete = "DELETE FROM student_table WHERE id = ?";
    String sqlCreate;

    // Список предметов
    Map<String, String[]> subjects = new HashMap<>();

    private final List<Student> studentList = new ArrayList<>();

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

    public Student getStudentById(int id) {
        for (Student student : studentList) {
            if (student.getId() == id) return student;
        }
        return null;
    }

    public void saveStudent(Student student) {
        try {
            student.dateList = new ArrayList<String>();
            student.subjectList = new ArrayList<String>();
            student.markList = new ArrayList<Integer>();
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
