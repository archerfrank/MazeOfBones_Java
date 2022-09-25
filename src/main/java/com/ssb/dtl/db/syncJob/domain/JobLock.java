package com.ssb.dtl.db.syncJob.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
@Entity
@NamedQueries(value = {
        @NamedQuery(name = "query_get_all_lockers",
                query = "Select c From JobLock c"),
        @NamedQuery(name = "query_get_all_busy_locker",
                query = "Select c From JobLock c where status = 'BUSY'") })
public class JobLock {
    public static String STATUS_NEW = "NEW";
    public static String STATUS_BUSY = "BUSY";
    public static String WITHDRAW_LOCKER_NAME = "WithDraw_Locker";
    public static String COMPLIANCE_LOCKER_NAME = "Compliance_Locker";
    public static String ACTIVITY_LOCKER_NAME = "Activity_Locker";
    public static String API_REQUEST_NAME="Api_Request_Locker";

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    private String usedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = true)
    private OffsetDateTime createdAt;
    @Column(name = "created_by", nullable = true)
    private String createdBy;
    @Column(name = "updated_at", nullable = true)
    private OffsetDateTime updatedAt;
    @Column(name = "updated_by", nullable = true)
    private String updatedBy;
}
