package com.springboot.demo.employeemanagementsystem.service;

import com.springboot.demo.employeemanagementsystem.entity.Addresses;
import com.springboot.demo.employeemanagementsystem.entity.User;
import com.springboot.demo.employeemanagementsystem.exception.UserNotFoundException;
import com.springboot.demo.employeemanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user-related operations.
 *
 * <p>This service provides methods to handle user data, including saving, retrieving, and checking
 * for the existence of users. It interacts with the {@link UserRepository} to perform database
 * operations.</p>
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Saves a new user in the system.
	 *
	 * <p>This method checks if a user already exists with the same first and last name. If an
	 * existing user is found, it throws an exception. Otherwise, it associates any addresses
	 * with the user and saves the user in the database.</p>
	 *
	 * @param theUser the {@link User} object to be saved
	 * @return the saved {@link User} object
	 * @throws Exception if a user with the same name already exists
	 */
	@Transactional
	public User saveUser(User theUser) throws Exception {
		Optional<Long> existingUserId = userExists(theUser);

		if (existingUserId.isPresent())
			throw new Exception("User already exists with ID: " + existingUserId.get());

		for (Addresses address : theUser.getAddresses())
			address.setUser(theUser);

		User savedUser = userRepository.save(theUser);
		return savedUser;
	}

	/**
	 * Retrieves all users from the database.
	 *
	 * @return a list of {@link User} objects
	 */
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * Retrieves a user by their ID.
	 *
	 * @param userId the ID of the user to be retrieved
	 * @return an {@link Optional} containing the {@link User} if found, or empty if not
	 */
	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

	/**
	 * Deletes a user by their ID from the database.
	 *
	 * This method checks if a user exists with the given ID. If the user is not found,
	 * a {@link UserNotFoundException} is thrown. If the user exists, the user is deleted.
	 *
	 * @param userId the ID of the user to be deleted
	 * @throws UserNotFoundException if no user exists with the specified ID
	 */
	@Transactional
	public void deleteById(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("User with id " + userId + " not found.");
		}else {
			userRepository.deleteById(userId);
		}
	}

	@Transactional
	public void updateUser(User theUser) throws Exception {

		for (Addresses address : theUser.getAddresses())
			address.setUser(theUser);

		 userRepository.save(theUser);

	}

	/**
	 * Checks if a user exists based on their first and last name.
	 *
	 * @param theUser the {@link User} object containing the first and last name
	 * @return an {@link Optional} containing the ID of the existing user if found, or empty if not
	 */
	private Optional<Long> userExists(User theUser) {
		return userRepository.existsByFirstNameAndLastName(theUser.getFirstName(), theUser.getLastName());
	}
}
