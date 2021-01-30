package com.insulinpump.entity;

import com.insulinpump.Util;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Misura {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    private Long id;
    @Getter
    private LocalDateTime date;
    @Getter
    private int lettura;

    protected Misura() {}

    public Misura(LocalDateTime date, int lettura) {
        this.date = date;
        this.lettura = lettura;
    }

    @Override
    public String toString() {
        return String.format(
                "Misure[id=%d, date='%s', lettura='%i']",
                id, Util.dateToSqliteString(date), lettura);
    }

}
