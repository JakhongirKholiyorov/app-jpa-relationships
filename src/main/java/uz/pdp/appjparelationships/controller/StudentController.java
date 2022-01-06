package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.*;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.payload.UniversityDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFacultyDean/{facultyId}")
    public Page<Student> getStudentListForFacultyDean(@PathVariable Integer facultyId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/forGroupOwner/{groupId}")
    public List<Student> getStudentListForGroupOwner(@PathVariable Integer groupId) {
        List<Student> students = studentRepository.findAllByGroupId(groupId);
        if (students == null){
            return null;
        }
        return students;
    }

    @PostMapping(value = "/student")
    public String addStudent(@RequestBody StudentDto studentDto) {
        Student student = new Student();

        student.setFirstName(studentDto.getFirstname());
        student.setLastName(studentDto.getLastname());

        Address address = new Address();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());
        //YASAB OLGAN ADDRESS OBJECTIMIZNI DB GA SAQLADIK VA U BIZGA SAQLANGAN ADDRESSNI BERDI
        Address savedAddress = addressRepository.save(address);

        student.setAddress(savedAddress);

        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());

        optionalGroup.ifPresent(student::setGroup);

        List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectIds());
        student.setSubjects(subjectList);

        studentRepository.save(student);

        return "student successfully added";
    }

    @PutMapping(value = "/student/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            Student student = optionalStudent.get();
            student.setFirstName(studentDto.getFirstname());
            student.setLastName(studentDto.getLastname());

            Address address = student.getAddress();
            address.setCity(studentDto.getCity());
            address.setDistrict(studentDto.getDistrict());
            address.setStreet(studentDto.getStreet());
            addressRepository.save(address);

            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (optionalGroup.isPresent()){
                Group group = optionalGroup.get();
                student.setGroup(group);
            }

            List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectIds());
            student.setSubjects(subjectList);

            studentRepository.save(student);
            return "student successfully edited";
        }
        return "student not found";
    }

    @DeleteMapping(value = "/student/{id}")
    public String deleteStudent(@PathVariable Integer id){
        studentRepository.deleteById(id);
        return "student deleted";
    }
}
