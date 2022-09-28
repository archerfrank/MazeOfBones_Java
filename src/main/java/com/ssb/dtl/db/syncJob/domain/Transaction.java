package com.ssb.dtl.db.syncJob.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
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
    private Instant blockTime;
    @CreationTimestamp
    private LocalDateTime createdDate;
    private String createdBy;
}
