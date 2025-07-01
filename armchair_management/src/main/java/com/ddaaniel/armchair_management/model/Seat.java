package com.ddaaniel.armchair_management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Random;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seatid")
    private UUID seatID;

    private Integer position;

    @Builder.Default
    private Boolean free = true;

    @OneToOne(/*cascade = CascadeType.PERSIST*/)
    @JoinColumn(name = "person_ID", unique = true)
    @JsonIgnoreProperties({"seat"}) // Evita loop infinito
    private Person person;


    @Override
    public String toString() {
        return "Seat{id=" + seatID + ", position=" + position + ", free=" + free + "}";
    }
}
