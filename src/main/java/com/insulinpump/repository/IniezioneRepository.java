package com.insulinpump.repository;

import java.util.List;

import com.insulinpump.entity.Iniezione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;

public interface IniezioneRepository extends PagingAndSortingRepository<Iniezione, Long> {

    List<Iniezione> findByDate(LocalDateTime date);

    Iniezione findById(long id);
    Iniezione deleteById(long id);
    @Query(value = "SELECT * FROM Iniezione i ORDER BY i.date DESC LIMIT ?1", nativeQuery = true)
    List<Iniezione> findNByOrderByDateDesc(int n);
}
