package com.ead.authuser.dtos;

import java.util.UUID;

import com.ead.authuser.enums.CourseLevel;
import com.ead.authuser.enums.CourseStatus;

import lombok.Data;


@Data
public class CourseDto {

	private UUID courseId;
	//@NotBlank
	private String name;    
	//@NotBlank
	private String description;
	private String imageUrl;
	//@NotNull
    private CourseStatus courseStatus;
	//@NotNull
    private CourseLevel CourseLevel;
	//@NotNull
	private UUID userInstructor;
	
}
