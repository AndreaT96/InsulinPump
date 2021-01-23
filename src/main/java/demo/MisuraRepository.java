package demo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface MisuraRepository extends PagingAndSortingRepository<Misura, Long> {

    Misura findById(long id);
    Misura deleteById(long id);
    @Query (value = "SELECT * FROM Misura m ORDER BY m.date DESC LIMIT ?1", nativeQuery = true)
    List<Misura> findNByOrderByDateDesc(int n);

}
