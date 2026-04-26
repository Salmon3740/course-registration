package com.course.registration.controller;

import com.course.registration.entity.Course;
import com.course.registration.entity.Enrollment;
import com.course.registration.entity.Student;
import com.course.registration.service.CourseService;
import com.course.registration.service.EnrollmentService;
import com.course.registration.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class WebController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    // --- Authentication & Landing Pages ---

    @GetMapping
    public String index(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/student/dashboard";
        }
        if (session.getAttribute("adminLoggedIn") != null) {
            return "redirect:/admin/dashboard";
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    private static final String ADMIN_SECRET_KEY = "COURSE_REGISTRATION_ADMIN";

    @PostMapping("/web/register")
    public String handleRegistration(@RequestParam String name, @RequestParam String email,
            @RequestParam String password,
            @RequestParam(value = "secretKey", required = false, defaultValue = "") String secretKey,
            RedirectAttributes redirectAttributes) {
        try {
            String role;
            if (!secretKey.isEmpty() && secretKey.equals(ADMIN_SECRET_KEY)) {
                role = "ADMIN";
            } else if (!secretKey.isEmpty()) {
                // Wrong key entered — register as STUDENT but warn
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You are not an admin. Registered as a Student instead.");
                role = "STUDENT";
            } else {
                role = "STUDENT";
            }

            Student student = new Student(name, email, password, role);
            studentService.saveStudent(student);

            if ("ADMIN".equals(role)) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Admin registration successful! You now have admin privileges. Please login.");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Registration successful. Please login.");
            }
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Email might already exist.");
            return "redirect:/register";
        }
    }

    @PostMapping("/web/login")
    public String handleLogin(@RequestParam String email, @RequestParam String password,
            HttpSession session, RedirectAttributes redirectAttributes) {
        // Legacy hardcoded admin (kept for backward compatibility)
        if ("admin@system.com".equals(email) && "admin123".equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin/dashboard";
        }

        Student student = studentService.login(email, password);
        if (student != null) {
            if ("ADMIN".equals(student.getRole())) {
                // Registered admin — grant admin session
                session.setAttribute("adminLoggedIn", true);
                session.setAttribute("adminName", student.getName());
                return "redirect:/admin/dashboard";
            } else {
                session.setAttribute("loggedInUser", student);
                return "redirect:/student/dashboard";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- Student Dashboard ---

    @GetMapping("/student/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        Student loggedInStudent = (Student) session.getAttribute("loggedInUser");
        if (loggedInStudent == null)
            return "redirect:/login";

        List<Course> allCourses = courseService.getAllCourses();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(loggedInStudent.getId());

        model.addAttribute("studentName", loggedInStudent.getName());
        model.addAttribute("courses", allCourses);
        model.addAttribute("myEnrollments", enrollments);
        return "student-dashboard";
    }

    @PostMapping("/web/enroll")
    public String enrollInCourse(@RequestParam Long courseId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Student loggedInStudent = (Student) session.getAttribute("loggedInUser");
        if (loggedInStudent == null)
            return "redirect:/login";

        try {
            enrollmentService.enrollStudent(loggedInStudent.getId(), courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    @PostMapping("/web/drop")
    public String dropCourse(@RequestParam Long courseId, HttpSession session, RedirectAttributes redirectAttributes) {
        Student loggedInStudent = (Student) session.getAttribute("loggedInUser");
        if (loggedInStudent == null)
            return "redirect:/login";

        try {
            enrollmentService.dropCourse(loggedInStudent.getId(), courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully dropped the course.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    // --- Admin Dashboard ---

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("adminLoggedIn") == null)
            return "redirect:/login";

        List<Course> allCourses = courseService.getAllCourses();
        model.addAttribute("courses", allCourses);
        return "admin-dashboard";
    }

    @PostMapping("/web/admin/course/add")
    public String addCourse(@RequestParam String courseName, @RequestParam int availableSeats,
            HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null)
            return "redirect:/login";

        try {
            Course course = new Course(courseName, availableSeats);
            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add course.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/web/admin/course/delete")
    public String deleteCourse(@RequestParam Long courseId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLoggedIn") == null)
            return "redirect:/login";

        try {
            courseService.deleteCourse(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete course with active enrollments.");
        }
        return "redirect:/admin/dashboard";
    }
}
