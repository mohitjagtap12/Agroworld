package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_status_history")
public class DeliveryStatusHistoryEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "delivery_job_id", nullable = false, length = 64)
    private String deliveryJobId;

    @Column(nullable = false, length = 64)
    private String status;

    @Column(name = "updated_by", nullable = false, length = 64)
    private String updatedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    public DeliveryStatusHistoryEntity() {}

    public DeliveryStatusHistoryEntity(String id, String deliveryJobId, String status, String updatedBy, String remarks) {
        this.id = id;
        this.deliveryJobId = deliveryJobId;
        this.status = status;
        this.updatedBy = updatedBy;
        this.remarks = remarks;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDeliveryJobId() { return deliveryJobId; }
    public void setDeliveryJobId(String deliveryJobId) { this.deliveryJobId = deliveryJobId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
