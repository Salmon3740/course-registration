$ErrorActionPreference = "Continue"
$rnd = Get-Random

Write-Host "1. Create Student"
$student = Invoke-RestMethod -Uri "http://localhost:8080/students" -Method Post -ContentType "application/json" -Body "{`"name`":`"John $rnd`", `"email`":`"john$rnd@test.com`", `"password`":`"password123`"}"
Write-Host "Student Created: $($student.id) - $($student.name)"

Write-Host "`n2. login Student"
$login = Invoke-RestMethod -Uri "http://localhost:8080/students/login" -Method Post -ContentType "application/json" -Body "{`"email`":`"john$rnd@test.com`", `"password`":`"password123`"}"
Write-Host "Student logged in: $($login.name)"

Write-Host "`n3. Create Course"
$course = Invoke-RestMethod -Uri "http://localhost:8080/courses" -Method Post -ContentType "application/json" -Body "{`"courseName`":`"Spring Boot $rnd`", `"availableSeats`":10}"
Write-Host "Course Created: $($course.id) - $($course.courseName) (Seats: $($course.availableSeats))"

Write-Host "`n4. Enroll Student"
$enrollment = Invoke-RestMethod -Uri "http://localhost:8080/enrollments/enroll" -Method Post -ContentType "application/json" -Body "{`"studentId`":$($student.id), `"courseId`":$($course.id)}"
Write-Host "Enrollment Created: $($enrollment.id)"

Write-Host "`n5. Check Course Seats After Enrollment"
$updatedCourse = Invoke-RestMethod -Uri "http://localhost:8080/courses/$($course.id)" -Method Get
Write-Host "Updated Seats: $($updatedCourse.availableSeats) (Expected 9)"

Write-Host "`n6. Check Enrolled Students for Course"
$enrolledStudents = Invoke-RestMethod -Uri "http://localhost:8080/enrollments/course/$($course.id)/students" -Method Get
Write-Host "Enrolled Students count: $($enrolledStudents.Count)"

Write-Host "`n7. Drop Course"
$dropResult = Invoke-RestMethod -Uri "http://localhost:8080/enrollments/drop" -Method Delete -ContentType "application/json" -Body "{`"studentId`":$($student.id), `"courseId`":$($course.id)}"
Write-Host "Drop Result: $dropResult"

Write-Host "`n8. Check Course Seats After Drop"
$finalCourse = Invoke-RestMethod -Uri "http://localhost:8080/courses/$($course.id)" -Method Get
Write-Host "Final Seats: $($finalCourse.availableSeats) (Expected 10)"

Write-Host "`n9. Delete Course"
$deleteResult = Invoke-RestMethod -Uri "http://localhost:8080/courses/$($course.id)" -Method Delete
Write-Host "Delete Result: $deleteResult"

Write-Host "`nALL TESTS COMPLETED!"
