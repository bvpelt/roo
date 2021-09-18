package nl.bsoft.roo.storage.repository;

import nl.bsoft.roo.storage.model.UserDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserDao, Long> {

    @RestResource(rel = "firstname-contains", path="firstname-contains")
    Page<UserDao> findByFirstNameContaining(@Param("query") String query, Pageable page);

    @RestResource(rel = "lastname-contains", path="lastname-contains", exported = false)
    Page<UserDao> findBylastNameContaining(@Param("query") String query, Pageable page);
}
