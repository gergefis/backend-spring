package com.europeandynamics.demo.employeemanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Addresses {

	public Addresses(String address) {
		this.address = address;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "address_id")
	private Long id;

	private String address;

	@Enumerated(EnumType.STRING)
	@Column(name = "address_type")
	private AddressType addressType;   // Enum WORK or HOME


	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
}