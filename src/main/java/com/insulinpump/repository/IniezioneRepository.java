package com.insulinpump.repository;

import com.insulinpump.entity.Iniezione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IniezioneRepository extends PagingAndSortingRepository<Iniezione, Long> {

    List<Iniezione> findByDate(LocalDateTime date);

    Iniezione findById(long id);
    Iniezione deleteById(long id);

    /**
     * Returns up to n rows from the DB. Results will be ordered by Date, in descending order
     * @param n - the amount of rows to fetch
     * @return a list containing the result of the query
     */
    @Query(value = "SELECT * FROM Iniezione i ORDER BY i.date DESC LIMIT ?1", nativeQuery = true)
    List<Iniezione> findNByOrderByDateDesc(int n);
}
