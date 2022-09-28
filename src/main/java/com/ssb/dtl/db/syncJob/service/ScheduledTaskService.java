package com.ssb.dtl.db.syncJob.service;

import com.ssb.dtl.db.syncJob.Constants;
import com.ssb.dtl.db.syncJob.HashAlgorithms;
import com.ssb.dtl.db.syncJob.domain.Account;
import com.ssb.dtl.db.syncJob.domain.Transaction;
import com.ssb.dtl.db.syncJob.schedule.task.TimerQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@Slf4j
public class ScheduledTaskService {
    @Autowired
    LockerServiceImp lockerServiceImp;
    @Value("${config.wallet.file}")
    String walletFile;
    @Value("${config.wallet.file.pass}")
    String walletPassword;
    @Value(("${config.network.client.endpoint}"))
    String endpoint;
    @Value(("${config.address.listen}"))
    String addressToListen;
    @Value(("${config.total.slot}"))
    int totalSlot;
    @Value(("${config.my.slot}"))
    int mySlot;
    @Autowired
    TimerQueue timerQueue;
    @Autowired
    SyncService syncService;
    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
//    @Scheduled(fixedDelay = 5000)
//    public void testFixedDelay(){
//        lockerServiceImp.queryOldBusyLock().forEach(lock -> {
//            log.info("busy expire lock {} ",lock);
//            lockerServiceImp.release(lock.getName());
//        });
//        log.info("try to get the lock ：" + sdf.format(System.currentTimeMillis()));
//        if (lockerServiceImp.tryLock(Constants.SYNC_JOB_LOCK)) {
//            log.info("Get the lock");
//            DelayTask t = new DelayTask("t", 40000, false) {
//                @Override
//                protected void execute0() {
//                    lockerServiceImp.release(Constants.SYNC_JOB_LOCK);
//                }
//            };
//            timerQueue.addTask(t);
//        }
//    }

    @Scheduled(fixedDelay = 120000)
    public void scanBlockTransactions(){
        try {
            lockerServiceImp.queryOldBusyLock().forEach(lock -> {
                log.info("busy expire lock {} ", lock);
                lockerServiceImp.release(lock.getName());
            });
            String lockName = Constants.SYNC_JOB_LOCK + "_" + mySlot;
            log.info("try to get the lock ：" + sdf.format(System.currentTimeMillis()));
            if (lockerServiceImp.tryLock(lockName)) {
                log.info("Get the lock");
                //TODO logic to sync
                try {
                    Web3j web3j = Web3j.build(new HttpService(
                            endpoint));
                    Credentials credentials =
                            WalletUtils.loadCredentials(
                                    walletPassword,
                                    walletFile);
                    long lastPosition = syncService.getLastPosition(mySlot);
                    log.info("Use wallet address {}, last position is {}", credentials.getAddress(), lastPosition);
                    final Event event = new Event("TransferEvent",
                            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {
                                                            },
                                    new TypeReference<Address>(true) {
                                    },
                                    new TypeReference<Uint256>() {
                                    },
                                    new TypeReference<Utf8String>() {
                                    },
                                    new TypeReference<Utf8String>() {
                                    },
                                    new TypeReference<Bool>() {
                                    },
                                    new TypeReference<Uint256>() {
                                    }));

                    EthFilter filter = new EthFilter(
                            DefaultBlockParameter.valueOf(BigInteger.valueOf(lastPosition)),
                            DefaultBlockParameterName.LATEST,
                            addressToListen);
                    filter.addSingleTopic(EventEncoder.encode(event));
                    web3j.ethLogFlowable(filter).subscribe(e -> {
                        try {
                            SyncService syncService = SpringUtils.getBean(SyncService.class);
                            EventValues eventValues = Contract.staticExtractEventParameters(event, e);
                            String fromAddr = eventValues.getIndexedValues().get(0).getValue().toString();
                            String toAddr = eventValues.getIndexedValues().get(1).getValue().toString();
                            BigDecimal amount = BigDecimal.valueOf(((BigInteger) eventValues.getNonIndexedValues().get(0).getValue()).longValue());
                            String fromName = eventValues.getNonIndexedValues().get(1).getValue().toString();
                            String toName = eventValues.getNonIndexedValues().get(2).getValue().toString();
                            Boolean airdrop = (Boolean) eventValues.getNonIndexedValues().get(3).getValue();
                            long time = ((BigInteger) eventValues.getNonIndexedValues().get(4).getValue()).longValue();
                            log.info("{},{},{},{},{},{},{}", fromAddr, toAddr, amount, fromName, toName, airdrop, time);
                            int hash = HashAlgorithms.additiveHash(fromAddr, totalSlot);
                            log.info("hash {},{},{}", hash, mySlot, totalSlot);
                            if (hash == mySlot) {
                                if (airdrop) {
                                    Account newAccount = Account.builder().address(toAddr).balance(BigDecimal.ZERO)
                                            .name(toName).build();
                                    try {
                                        syncService.saveAccount(newAccount);
                                    } catch (Throwable t) {
                                        log.error(t.getMessage(), t);
                                    }
                                }
                                Transaction newTrans = Transaction.builder().amount(amount)
                                        .blockHash(e.getBlockHash())
                                        .blockNumber(e.getBlockNumber().longValue())
                                        .fromAddr(fromAddr)
                                        .fromName(fromName)
                                        .toAddr(toAddr)
                                        .toName(toName)
                                        .blockTime(Instant.ofEpochSecond(time))
                                        .transactionHash(e.getTransactionHash())
                                        .createdBy(Constants.getCurrentIP())
                                        .build();
                                syncService.saveTransaction(newTrans,mySlot);
                            }
                        } catch (Throwable t) {
                            log.error(t.getMessage(), t);
                        }
                    });
                    Thread.sleep(1000 * 60 * 60 * 24 * 365);
                } catch (Throwable t){
                    log.error(t.getMessage(), t);
                    lockerServiceImp.release(lockName);
                }
            }
        } catch(Throwable t) {
            log.error(t.getMessage(), t);
        }
    }
}
