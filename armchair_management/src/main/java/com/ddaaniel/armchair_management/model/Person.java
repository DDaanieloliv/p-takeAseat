package com.ddaaniel.armchair_management.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "personid")
    private UUID personID;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 11, unique = true, nullable = false)
    private String cpf;

    @OneToOne(mappedBy = "person")
    //@JsonManagedReference
    private Seat seat;

    @Override
    public String toString() {
        return "Person{id=" + personID + ", name=" + name + ", cpf=" + cpf + "}";
    }

	// public UUID getPersonID() {
	// 	return personID;
	// }
	//
	// public void setPersonID(UUID personID) {
	// 	this.personID = personID;
	// }
	//
	// public String getName() {
	// 	return name;
	// }
	//
	// public void setName(String name) {
	// 	this.name = name;
	// }
	//
	// public String getCpf() {
	// 	return cpf;
	// }
	//
	// public void setCpf(String cpf) {
	// 	this.cpf = cpf;
	// }
	//
	// public Seat getSeat() {
	// 	return seat;
	// }
	//
	// public void setSeat(Seat seat) {
	// 	this.seat = seat;
	// }
}
