package com.example.javacore_springboot.repository;


import com.example.javacore_springboot.entity.CampaignSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignSummaryRepository extends JpaRepository<CampaignSummary, Long> {
    @Query(value = "select * from Campaign_summary cs order by cs.ctr desc limit 10" , nativeQuery = true)
    List<CampaignSummary>
    findTop10ByOrderByCtrDesc();

    @Query(value = "select * from Campaign_summary cs order by cs.cpa asc limit 10" , nativeQuery = true)
    List<CampaignSummary>
    findTop10ByCpaIsNotNullOrderByCpaAsc();
}
