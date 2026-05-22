CREATE DATABASE demo;
use demo;

CREATE TABLE Campaign (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          campaign_id VARCHAR(20),
                          date DATE,
                          impressions INT,
                          clicks INT,
                          spend DOUBLE,
                          conversions INT
);
-- load data vào bảng: import data from file --> mapping các trường --
EXPLAIN
SELECT
    campaign_id,
    SUM(impressions),
    SUM(clicks),
    SUM(spend),
    SUM(conversions)
FROM Campaign
GROUP BY campaign_id;

CREATE INDEX idx_campaign_cover
    ON Campaign (
                 campaign_id,
                 impressions,
                 clicks,
                 spend,
                 conversions
        );

SELECT
    campaign_id,
    SUM(impressions) total_impressions,
    SUM(clicks) total_clicks,
    SUM(spend) total_spend,
    SUM(conversions) total_conversions
FROM Campaign FORCE INDEX(idx_campaign_cover)
GROUP BY campaign_id;

drop table if exists Campaign_summary;
CREATE TABLE Campaign_summary (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  campaign_id VARCHAR(50),

                                  total_impressions BIGINT,
                                  total_clicks BIGINT,
                                  total_spend DECIMAL(15,6),
                                  total_conversions BIGINT,

                                  ctr DECIMAL(15,6),
                                  cpa DECIMAL(15,6),

                                  created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO Campaign_summary (
    campaign_id,
    total_impressions,
    total_clicks,
    total_spend,
    total_conversions,
    ctr,
    cpa
)
SELECT
    campaign_id,
    SUM(impressions) AS total_impressions,
    SUM(clicks) AS total_clicks,
    SUM(spend) AS total_spend,
    SUM(conversions) AS total_conversions,
    ROUND(
            SUM(clicks) / SUM(impressions),
            6
    ) AS ctr,
    CASE
        WHEN SUM(conversions) = 0 THEN NULL
        ELSE ROUND(
                SUM(spend) / SUM(conversions),
                6
             )
        END AS cpa
FROM Campaign
GROUP BY campaign_id;

select * from Campaign_summary;

select * from Campaign_summary cs order by cs.ctr desc;
