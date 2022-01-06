package uz.pdp.appjparelationships.payload;

import lombok.Data;

import java.util.List;

@Data
public class StudentDto {
    private String firstname;
    private String lastname;
    private String city;
    private String district;
    private String street;
    private Integer groupId;
    private List<Integer> subjectIds;
}
