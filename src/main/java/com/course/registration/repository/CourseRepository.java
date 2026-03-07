package com.course.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.course.registration.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

}