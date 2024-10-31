package com.springboot.demo.employeemanagementsystem.repository;

import com.springboot.demo.employeemanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;


@CrossOrigin("http://localhost:4200")
public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u.id FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
	Optional<Long> existsByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

//	Optional<Long> existsByFirstNameAndLastName(String firstName, String lastName);
}
