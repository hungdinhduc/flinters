package com.example.javacore_springboot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_summary")
public class CampaignSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_id", length = 50)
    private String campaignId;

    @Column(name = "total_impressions")
    private Long totalImpressions;

    @Column(name = "total_clicks")
    private Long totalClicks;

    @Column(name = "total_spend", precision = 20, scale = 6)
    private BigDecimal totalSpend;

    @Column(name = "total_conversions")
    private Long totalConversions;

    @Column(name = "ctr", precision = 20, scale = 6)
    private BigDecimal ctr;

    @Column(name = "cpa", precision = 20, scale = 6)
    private BigDecimal cpa;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public Long getTotalImpressions() {
        return totalImpressions;
    }

    public void setTotalImpressions(Long totalImpressions) {
        this.totalImpressions = totalImpressions;
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public BigDecimal getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(BigDecimal totalSpend) {
        this.totalSpend = totalSpend;
    }

    public Long getTotalConversions() {
        return totalConversions;
    }

    public void setTotalConversions(Long totalConversions) {
        this.totalConversions = totalConversions;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }

    public BigDecimal getCpa() {
        return cpa;
    }

    public void setCpa(BigDecimal cpa) {
        this.cpa = cpa;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
