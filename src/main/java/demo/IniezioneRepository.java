package demo;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;

public interface IniezioneRepository extends PagingAndSortingRepository<Iniezione, Long> {

    List<Iniezione> findByDate(LocalDateTime date);

    Iniezione findById(long id);
    Iniezione deleteById(long id);

}
