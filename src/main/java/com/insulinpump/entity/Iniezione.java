package com.insulinpump.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * JPA Entity class that represent a simple DB Table to store up the log of previous insulin injection
 */
@Entity
public class Iniezione {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private LocalDateTime date;
    @Getter
    @Setter
    private int unita;

    protected Iniezione() {}
    
    public Iniezione(LocalDateTime date, int unita) {
        this.date = date;
        this.unita = unita;
    }
}
