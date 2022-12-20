package antifraud.repository;

import antifraud.entity.StolenCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@EnableJpaRepositories
public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    @Query(value = "select * from stolen_card c where c.number = :number", nativeQuery = true)
    StolenCard findByNumber(@Param("number") String number);

    @Override
    List<StolenCard> findAll();
}
