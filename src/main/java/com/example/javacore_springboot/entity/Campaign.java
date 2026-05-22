package com.example.javacore_springboot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_id", length = 50)
    private String campaignId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "impressions")
    private Integer impressions;

    @Column(name = "clicks")
    private Integer clicks;

    @Column(name = "spend", precision = 15, scale = 2)
    private BigDecimal spend;

    @Column(name = "conversions")
    private Integer conversions;
}
