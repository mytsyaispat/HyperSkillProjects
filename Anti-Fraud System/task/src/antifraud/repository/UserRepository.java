package antifraud.repository;

import antifraud.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface UserRepository extends CrudRepository<User, Long> {
    @Query(name = "select * from users u where u.username like :username", nativeQuery = true)
    User findByUsername(@Param("username") String username);
}