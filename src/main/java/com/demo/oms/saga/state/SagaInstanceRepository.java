package com.demo.oms.saga.state;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, String> {
    Optional<SagaInstance> findByOrderId(String orderId);
    
    @Query("SELECT s FROM SagaInstance s WHERE s.status = 'STARTED' AND s.startedAt < ?1")
    List<SagaInstance> findTimedOutSagas(LocalDateTime timeout);
}
