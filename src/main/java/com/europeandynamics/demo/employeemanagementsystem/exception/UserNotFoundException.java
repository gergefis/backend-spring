package com.europeandynamics.demo.employeemanagementsystem.exception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message) {
		super(message);
	}
}
