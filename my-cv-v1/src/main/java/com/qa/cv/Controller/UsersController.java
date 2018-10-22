package com.qa.cv.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qa.cv.Exceptions.ResourceNotFoundException;
import com.qa.cv.Model.DepartmentModel;
import com.qa.cv.Model.UsersDataModel;
import com.qa.cv.Repositories.DepartmentRepository;
import com.qa.cv.Repositories.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("api/")
public class UsersController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	// Method to Create Users
	@PostMapping("/department/{departmentId}/user")
	public UsersDataModel createuser(@PathVariable(value = "departmentId") Long departmentId,
			@Valid @RequestBody UsersDataModel usersDataModel) {
		return departmentRepository.findById(departmentId).map(departmentModel -> {
			usersDataModel.setDepartment(departmentModel);
			return userRepository.save(usersDataModel);
		}).orElseThrow(() -> new ResourceNotFoundException("Department", "id", usersDataModel));
	}

	// Method to update role
	@PutMapping("/department/{departmentId}/user/{userId}")
	public UsersDataModel updateUserDepartment(@PathVariable(value = "departmentId") Long departmentId, @PathVariable(value = "userId") Long userId) 
	{
		UsersDataModel userModel = userRepository.findById(userId).get();
		return departmentRepository.findById(departmentId).map(departmentModel -> {
			userModel.setDepartment(departmentModel);
			return  userRepository.save(userModel);
		}).orElseThrow(() -> new ResourceNotFoundException("Department", "id", userModel));
	}
		
	// Method to get a user
	@GetMapping("/user/{userId}")
	public UsersDataModel getUserByUserId(@PathVariable(value = "userId") Long userId, Pageable pageable) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("UserModel", "id", userId));
	}
	
	// Method to get a user by name
	@GetMapping("findbyname/{name}&{lastName}")
	public List<UsersDataModel> getAllUsersByName(@PathVariable(value = "name") String name,
			@PathVariable(value = "lastName") String lastName, Pageable pageable) {

		List<UsersDataModel> user = userRepository.findAll();
		user = user.stream().filter(u -> {
			if (u.getFirstName().toLowerCase().startsWith(name.toLowerCase()) 
					&& u.getLastName().toLowerCase().startsWith(lastName.toLowerCase())) {
				return true;
			}
			if (lastName.equals("")) {

				if (u.getFirstName().toLowerCase().startsWith(name.toLowerCase()) 
						|| u.getLastName().toLowerCase().startsWith(name.toLowerCase())) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		return user;
	}

	// Method to get user with email and password (Log in)
	@GetMapping("/login/{email}&{password}")
	public Page<UsersDataModel> getAllUsersByEmail(@PathVariable(value = "email") String email,
			@PathVariable(value = "password") String password, Pageable pageable) {

		Page<UsersDataModel> user = userRepository.findByEmail(email, pageable);
		if (!user.getContent().get(0).getPassword().toString().equals(password)) {
			throw new ResourceNotFoundException(email, email, null);
		}
		return user;
	}

	// Method to Get all Users in a given department
	@GetMapping("/department/{departmentId}/user")
	public Page<UsersDataModel> getAllUsersByDepartmentId(
			@PathVariable(value = "departmentId") DepartmentModel departmentId, Pageable pageable) {
		return userRepository.findByDepartmentId(departmentId, pageable);
	}

	// Method to get all users
	@GetMapping("/user")
	public List<UsersDataModel> getAlluser() {
		return userRepository.findAll();
	}

	// Method to Edit a user
	@PutMapping("/user/{userId}")
	public UsersDataModel updateUser(@PathVariable(value = "userId") Long userId, @Valid @RequestBody UsersDataModel userRequest) {
		return userRepository.findById(userId).map(user -> {
			user.setFirstName(userRequest.getFirstName());
			user.setLastName(userRequest.getLastName());
			user.setEmail(userRequest.getEmail());
			user.setPassword(userRequest.getPassword());
			return userRepository.save(user);
		}).orElseThrow(() -> new ResourceNotFoundException("User", "id", userRequest));
	}

	// Method to remove a user
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable(value = "userId") Long userId) {
		return userRepository.findById(userId).map(user -> {
			userRepository.delete(user);
			return ResponseEntity.ok().build();
		}).orElseThrow(() -> new ResourceNotFoundException("UserId", userId.toString(), null));

	}

}
