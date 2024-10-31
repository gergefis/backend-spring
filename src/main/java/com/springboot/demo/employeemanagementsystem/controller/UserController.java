package com.springboot.demo.employeemanagementsystem.controller;

import com.springboot.demo.employeemanagementsystem.entity.User;
import com.springboot.demo.employeemanagementsystem.exception.UserNotFoundException;
import com.springboot.demo.employeemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing user-related operations.
 *
 * This controller provides endpoints for performing CRUD operations on users. It handles
 * user registration, retrieval of all users, retrieval of users by ID, and error handling.
 *
 * Key Endpoints:
 *   POST /registerUser</b>: Registers a new user in the system.
 *   GET /getUsers</b>: Retrieves all users.
 *   <b>GET /{id}</b>: Retrieves a user by their ID.
 *
 * Each method returns an appropriate HTTP response status based on the outcome of the operation,
 * including success (200 OK or 201 CREATED) and failure (404 NOT FOUND, 409 CONFLICT).
 *
 * Handles exceptions where necessary to ensure robust error management.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private final UserService userService;

	private Map<String, String> response = new HashMap<>();
	private Map<String, Object> responseObject = new HashMap<>();

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Registers a new user in the system.
	 *
	 * This method handles HTTP POST requests to the "/registerUser" endpoint. It receives a
	 * {@link User} object from the request body and attempts to save the user using the
	 * {@link UserService}. If the operation is successful, it returns a 201 CREATED status along with
	 * the saved user data. If any exception occurs during the saving process, it catches the exception
	 * and returns a 409 CONFLICT status with the error message.
	 *
	 * @param theUser the {@link User} object to be registered, provided in the request body
	 * @return a {@link ResponseEntity} containing the registered user and a status of 201 CREATED
	 *         if successful, or a 409 CONFLICT status with an error message if there is a failure
	 * @throws Exception if an error occurs while saving the user, handled within the method
	 */
	@PostMapping("/registerUser")
	public ResponseEntity<?> addUser(@RequestBody User theUser) {
		try {
			User userDetails = userService.saveUser(theUser);
			return new ResponseEntity<>(userDetails, HttpStatus.CREATED);
		} catch(Exception e) {
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}
	}

	/**
	 * Retrieves a user by their ID.
	 *
	 * This method handles HTTP GET requests to the "/{id}" endpoint. It fetches a {@link User}
	 * from the system based on the provided user ID. If a user with the specified ID is found,
	 * it returns a 200 OK status along with the user data. If no user is found, it returns a
	 * 404 NOT FOUND status.
	 *
	 * @param userId the ID of the {@link User} to be retrieved, provided as a path variable
	 * @return a {@link ResponseEntity} containing the {@link User} object and a status of 200 OK
	 *         if the user is found, or a 404 NOT FOUND status if no user with the specified ID exists
	 */
	@RequestMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long userId) {
		return userService.findById(userId)
					.map(user -> ResponseEntity.ok(user)) //ResponseEntity::ok
					.orElseThrow(() -> new UserNotFoundException("The user does not exist."));
	}

	/**
	 * Retrieves all users from the system.
	 *
	 * This method handles HTTP GET requests to the "/getUsers" endpoint. It retrieves a list of all
	 * users from the system using the {@link UserService}. If the operation is successful, it returns a
	 * 200 OK status with the list of users. If an exception occurs, it catches the exception and
	 * returns a 404 NOT FOUND status with an error message.
	 *
	 * @return a {@link ResponseEntity} containing a map with a list of users under the key "users" and
	 *         a status of 200 OK if successful, or a 404 NOT FOUND status with an error message
	 *         in case of failure
	 * @throws Exception if an error occurs while retrieving the users, handled within the method
	 */
	@GetMapping("/getUsers")
	public ResponseEntity<Map<String, Object>> getUsers() {
		try {
				List<User> users = userService.getAllUsers();
				responseObject.put("users", users);
				return ResponseEntity.ok(responseObject);
		}catch (Exception e) {
			responseObject.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseObject);
		}
	}

	@PutMapping("/updateUser")
	public void updateEmployee(@RequestBody User theUser) throws Exception {

		userService.updateUser(theUser);
	}

	/**
	 * Deletes a user by their ID.
	 *
	 * This method handles DELETE requests to "/deleteUser/{id}".
	 * It attempts to delete the user from the database based on the provided user ID.
	 * If the user is successfully deleted, a success message is returned. If the user
	 * does not exist, or if an error occurs during deletion, appropriate error messages
	 * are returned.
	 *
	 * @param userId The ID of the user to be deleted.
	 * @return A ResponseEntity containing a success or error message.
	 *         On success: HTTP 200 (OK) with a message "User deleted successfully".
	 *         On user not found: HTTP 404 (NOT FOUND) with a message "User with id = {id} not found".
	 *         On server error: HTTP 500 (INTERNAL SERVER ERROR) with a message "Error occurred during deletion".
	 */
	@DeleteMapping("/deleteUser/{id}")
	public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") Long userId) {
		response.clear();
		try {
			userService.deleteById(userId);
			response.put("message", "User deleted successfully");
			return ResponseEntity.ok(response);
		} catch (UserNotFoundException e) {
			response.put("not_found_message", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		catch (Exception e) {
			response.put("error", "Error occurred during deletion: " + e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		}
	}

}
