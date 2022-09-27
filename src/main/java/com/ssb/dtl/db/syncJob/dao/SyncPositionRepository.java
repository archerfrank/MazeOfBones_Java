package com.ssb.dtl.db.syncJob.dao;

import com.ssb.dtl.db.syncJob.domain.SyncPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncPositionRepository extends JpaRepository<SyncPosition, Long> {
    @Modifying
    @Query("update SyncPosition sp set sp.blockNumber = ?1, lastUpdatedDate=NOW()  where id=?2")
    public void updatePosition(Long position, Long id);
}
