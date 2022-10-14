package com.ssb.dtl.db.syncJob.dao;

import com.ssb.dtl.db.syncJob.Constants;
import com.ssb.dtl.db.syncJob.domain.JobLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class LockerRepository {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EntityManager em;

    public boolean tryLock(String name){
        Query q = em.createNativeQuery("update job_lock set status = 'BUSY', updated_at = current_timestamp, used_by = ? where name = ? and status = 'NEW'");
        q.setParameter(1, Constants.getCurrentIP());
        q.setParameter(2, name);
        return q.executeUpdate() == 1;

    }

    @Transactional
    public boolean renew(String name){
        Query q = em.createNativeQuery("update job_lock set updated_at = current_timestamp where name = ? and used_by = ? and status = 'BUSY'");
        q.setParameter(1, name);
        q.setParameter(2, Constants.getCurrentIP());
        return q.executeUpdate() == 1;

    }

    public boolean release(String name) {
        Query q = em.createNativeQuery("update job_lock set status = 'NEW', updated_at = current_timestamp where name = ? and used_by = ?");
        q.setParameter(1, name);
        q.setParameter(2, Constants.getCurrentIP());
        return q.executeUpdate() == 1;
    }

    public boolean releaseBusyLock(JobLock lock) {
        Query q = em.createNativeQuery("update job_lock set status = 'NEW', updated_at = current_timestamp where name = ? and updated_at = ?");
        q.setParameter(1, lock.getName());
        q.setParameter(2, lock.getUpdatedAt());
        return q.executeUpdate() == 1;
    }

    public List<JobLock> getBusyLocker(){
        Query q = em.createNamedQuery("query_get_all_busy_locker", JobLock.class);
        return q.getResultList();
    }
}
