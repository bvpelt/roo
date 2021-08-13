package nl.bsoft.roo.storage.repository;

import nl.bsoft.roo.storage.model.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDao, Long> {
}
