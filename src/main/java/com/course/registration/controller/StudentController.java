package com.course.registration.controller;

import com.course.registration.entity.Student;
import com.course.registration.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping("/login")
    public Student login(@RequestBody com.course.registration.payload.LoginRequest loginRequest) {
        Student student = studentService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (student == null) {
            throw new RuntimeException("Invalid email or password");
        }
        return student;
    }
}