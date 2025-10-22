package com.ddaaniel.armchair_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(length = 11, /* unique = true, */ nullable = false)
    private String cpf;

    @OneToOne(mappedBy = "person")
    //@JsonManagedReference
    private Seat seat;

    @Override
    public String toString() {
        return "Person{id=" + personID + ", name=" + name + ", cpf=" + cpf + "}";
    }

}
