package com.insulinpump.unittest.repository;

import com.insulinpump.entity.Misura;
import com.insulinpump.repository.MisuraRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class MisuraRepositoryTest {

    @Autowired
    private MisuraRepository misuraRepository;


    @Test
    public void newMisuraTest() {
        Misura misura = new Misura(LocalDateTime.now(), 1);
        Misura saved = misuraRepository.save(misura);
        assertEquals("IDs should match", misura.getId(), saved.getId());
        assertNotNull("Added misura should be fetchable", misuraRepository.findById(saved.getId()));
    }

    @Test
    public void getLastNTest() {
        misuraRepository.deleteAll();
        for(int i = 0; i < 5; i++) {
            misuraRepository.save(new Misura(LocalDateTime.now(), 1));
        }
        assertEquals(5, misuraRepository.count());
        assertEquals("It should've fetched 5 rows", misuraRepository.count(), misuraRepository.findNByOrderByDateDesc(5).size());
        assertEquals("It should've fetched 5 rows", misuraRepository.count(), misuraRepository.findNByOrderByDateDesc(100).size());
    }

    @Test
    public void deleteAllTest() {
        misuraRepository.save(new Misura(LocalDateTime.now(), 1));
        misuraRepository.deleteAll();
        assertEquals("Table should be empty now", 0, misuraRepository.count());

    }

    @Test
    public void editTest() {
        Misura i = misuraRepository.save(new Misura(LocalDateTime.now(), 1));
        i.setDate(LocalDateTime.of(2000,1,1,1,1));
        i.setLettura(2);
        i.setId(i.getId());
        i = misuraRepository.save(i);
        assertEquals("Lettura should be 2 now", 2, i.getLettura());
        assertEquals(0, ChronoUnit.MICROS.between(i.getDate(), LocalDateTime.of(2000, 1, 1, 1, 1)));

    }
}
