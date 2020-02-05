package com.algo.vn30.persistence;

import com.algo.vn30.entity.FreeFloatDataImpl;
import com.algo.vn30.entity.SecurityImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FreeFloatDataPersistence extends CrudRepository<FreeFloatDataImpl, Long> {

    @Query("SELECT t FROM FreeFloatDataImpl t WHERE t.sec_id = :sec_id")
    List<FreeFloatDataImpl> findBySecId(@Param("sec_id") Long sec_id);

    @Query("SELECT t FROM FreeFloatDataImpl t WHERE t.sec_id = :sec_id and t.date = :date")
    List<FreeFloatDataImpl> findBySecIdAndDate(@Param("sec_id") Long sec_id, @Param("date") Date date);

}
