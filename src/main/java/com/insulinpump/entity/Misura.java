package com.insulinpump.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
/**
 * JPA Entity class that represent a simple DB Table to store up the log of previous sensor readings
 */
@Entity
public class Misura {

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
    private int lettura;

    protected Misura() {}

    public Misura(LocalDateTime date, int lettura) {
        this.date = date;
        this.lettura = lettura;
    }
}
