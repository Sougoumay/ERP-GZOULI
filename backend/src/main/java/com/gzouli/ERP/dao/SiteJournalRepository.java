package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.SiteJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SiteJournalRepository extends JpaRepository<SiteJournal, Long> {
    List<SiteJournal> findByProjectIdOrderByWorkDateDesc(Long projectId);



    List<SiteJournal> findByProjectIdAndWorkDateBetweenOrderByWorkDateAsc(Long projectId, LocalDate startDate, LocalDate endDate);
}
