package com.insulinpump.entity;

import com.insulinpump.Util;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Iniezione {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;
    @Getter
    private LocalDateTime date;
    @Getter
    private int unita;

    protected Iniezione() {}

    public Iniezione(LocalDateTime date, int unita) {
        this.date = date;
        this.unita = unita;
    }
}
