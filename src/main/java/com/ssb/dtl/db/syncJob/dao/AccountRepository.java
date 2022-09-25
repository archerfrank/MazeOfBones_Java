package com.ssb.dtl.db.syncJob.dao;

import com.ssb.dtl.db.syncJob.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Modifying
    @Transactional
    @Query("update Account a set a.balance = balance + ?2, a.lastUpdatedDate=NOW()  where a.address=?1")
    public void updateBalance(String addr, BigDecimal amount);
}
