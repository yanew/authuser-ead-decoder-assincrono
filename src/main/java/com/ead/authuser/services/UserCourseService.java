package com.ead.authuser.services;

import java.util.UUID;

import com.ead.authuser.models.UserCourseModel;
import com.ead.authuser.models.UserModel;

public interface UserCourseService {

	public boolean existsByUserAndCourseId(UserModel userModel, UUID courseId);

	public UserCourseModel save(UserCourseModel userCourseModel);

	public boolean existsByCourseId(UUID courseId);

	public void deleteUserCourseByCourse(UUID courseId);

}
