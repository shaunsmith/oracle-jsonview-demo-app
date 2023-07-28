
package com.example.micronaut.controller;

import java.util.List;
import java.util.Optional;

import com.example.micronaut.dto.CreateStudentViewDto;
import com.example.micronaut.entity.Class;
import com.example.micronaut.entity.Student;
import com.example.micronaut.entity.view.StudentScheduleClassView;
import com.example.micronaut.entity.view.StudentScheduleView;
import com.example.micronaut.entity.view.StudentView;
import com.example.micronaut.repository.ClassRepository;
import com.example.micronaut.repository.StudentRepository;
import com.example.micronaut.repository.view.StudentViewRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;

@Controller("/students")
public final class StudentController {

    private final StudentViewRepository studentViewRepository;
    private final ClassRepository classRepository;

    private final StudentRepository studentRepository;

    public StudentController(StudentViewRepository studentViewRepository, ClassRepository classRepository, StudentRepository studentRepository) {
        this.studentViewRepository = studentViewRepository;
        this.classRepository = classRepository;
        this.studentRepository = studentRepository;
    }

    @Get("/{id}")
    public Optional<StudentView> findById(Long id) {
        return studentViewRepository.findById(id);
    }

    @Get("/")
    public Iterable<StudentView> findAll() {
        return studentViewRepository.findAll();
    }

    @Get("/student/{student}")
    public Optional<StudentView> findByStudent(@NonNull String student) {
        return studentViewRepository.findByStudent(student);
    }

    @Put("/{id}/average_grade/{averageGrade}")
    public Optional<StudentView> updateAverageGrade(Long id, @NonNull Double averageGrade) {
        return studentViewRepository.findById(id).flatMap(studentView -> {
            studentViewRepository.updateAverageGrade(id, averageGrade);
            return studentViewRepository.findById(id);
        });
    }

    @Put("/{id}/student/{student}")
    public Optional<StudentView> updateStudent(Long id, @NonNull String student) {
        return studentViewRepository.findById(id).flatMap(studentView -> {
            studentViewRepository.updateStudentByStudentId(id, student);
            return studentViewRepository.findById(id);
        });
    }

    @Post("/")
    @Status(HttpStatus.CREATED)
    public Optional<StudentView> create(@NonNull @Body CreateStudentViewDto createDto) {
        List<Class> classes = classRepository.findByNameIn(createDto.classes());
        Student entity = this.studentRepository.save(new Student(
                null,
                createDto.student(),
                createDto.averageGrade(),
                classes
        ));
        List<StudentScheduleView> studentScheduleViews = classes.stream()
                .map(c -> new StudentScheduleView(new StudentScheduleClassView(c))).toList();
        StudentView studentView = new StudentView(
                entity.id(),
                createDto.student(),
                createDto.averageGrade(),
                studentScheduleViews,
                null
        );
        return Optional.of(studentView);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    void delete(Long id) {
        studentViewRepository.deleteById(id);
    }

    @Get("/max_average_grade")
    Optional<Double> findMaxAverageGrade() {
        return studentViewRepository.findMaxAverageGrade();
    }
}
