package com.ssb.dtl.db.syncJob.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"transactionHash"})})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionHash;
    private String blockHash;
    private long blockNumber;
    private String fromAddr;
    private String toAddr;
    private BigDecimal amount;
    private String fromName;
    private String toName;
    @CreationTimestamp
    private LocalDateTime createdDate;
}