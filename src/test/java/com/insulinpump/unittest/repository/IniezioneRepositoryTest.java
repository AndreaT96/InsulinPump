package com.insulinpump.unittest.repository;


import com.insulinpump.entity.Iniezione;
import com.insulinpump.repository.IniezioneRepository;
import org.junit.Before;
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
public class IniezioneRepositoryTest {
    @Autowired
    private IniezioneRepository iniezioneRepository;

    @Before
    public void init(){
        assertNotNull("Repository shouldn't be null", iniezioneRepository);
    }

    /**
     * This test aims to check the
     */
    @Test
    public void newIniezioneTest() {
        Iniezione iniezione = new Iniezione(LocalDateTime.now(), 1);
        Iniezione saved = iniezioneRepository.save(iniezione);
        assertEquals("IDs should match", iniezione.getId(), saved.getId());
        assertNotNull("Added iniezione should be fetchable", iniezioneRepository.findById(saved.getId()));
    }

    @Test
    public void getLastNTest() {
        iniezioneRepository.deleteAll();
        for(int i = 0; i < 5; i++) {
            iniezioneRepository.save(new Iniezione(LocalDateTime.now(), 1));
        }
        assertEquals(5, iniezioneRepository.count());
        assertEquals("It should've fetched 5 rows", iniezioneRepository.count(), iniezioneRepository.findNByOrderByDateDesc(5).size());
        assertEquals("It should've fetched 5 rows", iniezioneRepository.count(), iniezioneRepository.findNByOrderByDateDesc(100).size());
    }

    @Test
    public void deleteAllTest() {
        iniezioneRepository.save(new Iniezione(LocalDateTime.now(), 1));
        iniezioneRepository.deleteAll();
        assertEquals("Table should be empty now", 0, iniezioneRepository.count());
    }

    @Test
    public void editTest() {
        Iniezione i = iniezioneRepository.save(new Iniezione(LocalDateTime.now(), 1));
        i.setDate(LocalDateTime.of(2000,1,1,1,1));
        i.setUnita(2);
        i.setId(i.getId());
        i = iniezioneRepository.save(i);
        assertEquals("Unita should be 2 now", 2, i.getUnita());
        assertEquals(0, ChronoUnit.MICROS.between(i.getDate(), LocalDateTime.of(2000, 1, 1, 1, 1)));

    }
}
