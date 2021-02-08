package com.insulinpump.repository;

import com.insulinpump.entity.Misura;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface MisuraRepository extends PagingAndSortingRepository<Misura, Long> {

    Misura findById(long id);
    Misura deleteById(long id);

    /**
     * Returns up to n rows from the DB. Results will be ordered by Date, in descending order
     * @param n - the amount of rows to fetch
     * @return a list containing the result of the query
     */
    @Query (value = "SELECT * FROM Misura m ORDER BY m.date DESC LIMIT ?1", nativeQuery = true)
    List<Misura> findNByOrderByDateDesc(int n);

}
