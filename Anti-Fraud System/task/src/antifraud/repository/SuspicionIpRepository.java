package antifraud.repository;

import antifraud.entity.SuspicionIp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@EnableJpaRepositories
public interface SuspicionIpRepository extends CrudRepository<SuspicionIp, Long> {
    @Query(value = "select * from suspicion_ip s where s.ip = :ip", nativeQuery = true)
    SuspicionIp findByIp(@Param("ip") String ip);

    @Override
    List<SuspicionIp> findAll();
}
