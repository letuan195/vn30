package com.algo.vn30.persistence;

import com.algo.vn30.entity.FreeFloatRealDataImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FreeFloatRealDataPersistence extends CrudRepository<FreeFloatRealDataImpl, Long> {

    @Query("SELECT t FROM FreeFloatRealDataImpl t WHERE t.sec_id = :sec_id")
    List<FreeFloatRealDataImpl> findBySecId(@Param("sec_id") Long sec_id);

    @Query("SELECT t FROM FreeFloatRealDataImpl t WHERE t.sec_id = :sec_id and t.date = :date")
    List<FreeFloatRealDataImpl> findBySecIdAndDate(@Param("sec_id") Long sec_id, @Param("date") Date date);

    @Query("SELECT t FROM FreeFloatRealDataImpl t WHERE t.date = :date")
    List<FreeFloatRealDataImpl> findByDate(@Param("date") Date date);

    @Query("SELECT distinct t.date FROM FreeFloatRealDataImpl t WHERE t.date <= :date order by t.date desc")
    List<Date> findNewestByDate(@Param("date") Date date);

}
