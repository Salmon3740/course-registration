package com.course.registration.service;

import com.course.registration.entity.Student;
import com.course.registration.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student login(String email, String password) {
        Student student = studentRepository.findByEmail(email).orElse(null);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        return null;
    }
}