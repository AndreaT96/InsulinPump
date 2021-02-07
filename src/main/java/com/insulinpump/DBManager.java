package com.insulinpump;

import com.insulinpump.entity.Iniezione;
import com.insulinpump.entity.Misura;
import com.insulinpump.repository.IniezioneRepository;
import com.insulinpump.repository.MisuraRepository;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public class DBManager {

    private MisuraRepository misuraRepository;
    private IniezioneRepository iniezioneRepository;

    public DBManager(MisuraRepository misuraRepository, IniezioneRepository iniezioneRepository) {
        this.misuraRepository = misuraRepository;
        this.iniezioneRepository = iniezioneRepository;
    }

    /**
     * Inserts a sugar reading into the "misura" table
     * @param level of sugar measured by the sensor
     * @param date of the reading
     * @return the id of the record
     * @throws IllegalArgumentException in case the date object is null
     * @throws NullPointerException in case the save fails and results in an empty Entity
     */
    public Long sqlIteInsertReading(int level, @NonNull LocalDateTime date) throws IllegalArgumentException, NullPointerException {
        return misuraRepository.save(new Misura(date, level)).getId();
    }

    /**
     * Inserts the amount of insulin injected in a certain date into the "iniezioni" table
     * @param amount of insulin units injected.
     * @param date of the injection
     * @return the id of the record
     * @throws IllegalArgumentException in case the date object is null
     * @throws NullPointerException in case the save fails and results in an empty Entity
     */
    public Long sqlIteInsertInjection(int amount, @NonNull LocalDateTime date) throws IllegalArgumentException, NullPointerException {
        return iniezioneRepository.save(new Iniezione(date, amount)).getId();
    }

    /**
     * Returns the latest N sensor readings. If there are less rows than N then all readings will be returned.
     * @param n the non-negative amount of readings to return.
     * @return a ResultSet containing the camps [id, date, lettura]
     */
    public List<Misura> getLastNReadings(int n){
        if(n < 0) return null;
        return misuraRepository.findNByOrderByDateDesc(n);
    }
}
