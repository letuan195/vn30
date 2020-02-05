package com.algo.vn30.persistence;

import com.algo.vn30.entity.SecurityImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityPersistence extends CrudRepository<SecurityImpl, Long> {

    @Query("SELECT t FROM SecurityImpl t WHERE t.name = :name")
    List<SecurityImpl> findByName(@Param("name") String name);

    @Query("SELECT t FROM SecurityImpl t WHERE t.id = :id")
    List<SecurityImpl> findBySecId(@Param("id") Long id);

    @Query("SELECT t FROM SecurityImpl t WHERE t.exchange = :exchange")
    List<SecurityImpl> findByExchange(@Param("exchange") String exchange);

    @Query("SELECT t FROM SecurityImpl t WHERE t.exchange = :exchange and t.id < :id")
    List<SecurityImpl> findByTest(@Param("exchange") String exchange, @Param("id") Long id);
}
