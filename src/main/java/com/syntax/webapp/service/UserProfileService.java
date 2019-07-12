package com.syntax.webapp.service;

import java.util.List;

import com.syntax.webapp.model.UserProfile;


public interface UserProfileService {

	UserProfile findById(int id);

	UserProfile findByType(String type);
	
	List<UserProfile> findAll();
	
}
