package antifraud.repository;

import antifraud.entity.TransactionEntity;
import antifraud.entity.response.TransactionResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {
    @Query( value = "SELECT count(DISTINCT t.ip) FROM transaction t WHERE t.number = :number AND t.ip <> :ip AND t.date BETWEEN :from_date AND :to_date",
            nativeQuery = true)
    int countUniqueTransactionsByIp(
            @Param(value = "number") String number,
            @Param(value = "ip") String ip,
            @Param(value = "from_date") LocalDateTime fromDate,
            @Param(value = "to_date") LocalDateTime toDate);


    @Query( value = "SELECT count(DISTINCT t.region) FROM transaction t WHERE t.number = :number AND t.region <> :region AND t.date BETWEEN :from_date AND :to_date",
            nativeQuery = true)
    int countUniqueTransactionsByRegion(
            @Param(value = "number") String number,
            @Param(value = "region") String region,
            @Param(value = "from_date") LocalDateTime fromDate,
            @Param(value = "to_date") LocalDateTime toDate);

    Optional<TransactionEntity> findByTransactionId(long transactionId);


    List<TransactionEntity> findAll();
    List<TransactionEntity> findAllByNumber(String number);

}
