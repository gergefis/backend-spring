package com.europeandynamics.demo.employeemanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {

	public User(String firstName, String lastName, Gender gender, Date birthdate) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.birthdate = birthdate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(TemporalType.DATE)
	private Date birthdate;

	@OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
												CascadeType.DETACH, CascadeType.REFRESH},
													orphanRemoval = true)
		private List<Addresses> addresses;

	public void addAddress(Addresses tempAddress) {
		if (addresses == null) {
			addresses = new ArrayList<>();
		}

		if (!addresses.contains(tempAddress)) {
//			addresses.add(tempAddress);
			tempAddress.setUser(this);
		}
	}

}
