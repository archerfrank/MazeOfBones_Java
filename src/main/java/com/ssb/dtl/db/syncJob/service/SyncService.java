package com.ssb.dtl.db.syncJob.service;

import com.ssb.dtl.db.syncJob.dao.AccountRepository;
import com.ssb.dtl.db.syncJob.dao.SyncPositionRepository;
import com.ssb.dtl.db.syncJob.dao.TransactionRepository;
import com.ssb.dtl.db.syncJob.domain.Account;
import com.ssb.dtl.db.syncJob.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    SyncPositionRepository syncPositionRepository;

    @Transactional
    void saveTransaction(Transaction trans) {
        accountRepository.updateBalance(trans.getFromAddr(), trans.getAmount().negate());
        accountRepository.updateBalance(trans.getToAddr(), trans.getAmount());
        transactionRepository.save(trans);
        syncPositionRepository.updatePosition(trans.getBlockNumber(), 1000L);

    }

    @Transactional(readOnly = true)
    public long getLastPosition(long id) {
        return syncPositionRepository.findById(id).map(x -> x.getBlockNumber()).orElse(1000L);
    }

    @Transactional
    void saveAccount(Account acct) {
        accountRepository.save(acct);
    }
}
