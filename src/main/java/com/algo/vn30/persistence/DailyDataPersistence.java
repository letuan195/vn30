package com.algo.vn30.persistence;

import com.algo.vn30.entity.DailyDataImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DailyDataPersistence extends CrudRepository<DailyDataImpl, Long> {

    @Query("SELECT t FROM DailyDataImpl t WHERE t.sec_id = :sec_id ORDER BY t.date desc")
    List<DailyDataImpl> findBySecId(@Param("sec_id") Long sec_id);

    @Query("SELECT t FROM DailyDataImpl t WHERE t.sec_id = :sec_id and t.date = :date")
    List<DailyDataImpl> findBySecIdAndThisDate(@Param("sec_id") Long sec_id, @Param("date") Date date);

    @Query("SELECT t FROM DailyDataImpl t WHERE t.sec_id = :sec_id and t.date > :start_date and t.date <= :end_date order by t.date desc")
    List<DailyDataImpl> findBySecIdAndDateAfter(@Param("sec_id") Long sec_id, @Param("start_date") Date start_date, @Param("end_date") Date end_date);

}
