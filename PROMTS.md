# PROMTS.md

> Tổng hợp các prompt/câu hỏi đã dùng trong cuộc trò chuyện, từ lúc tạo bảng summary đến lúc chạy project và export file CSV.

---

# 1. Tạo bảng dữ liệu campaign

## Prompt

```text
campaign_id string Campaign ID

date string Date in YYYY-MM-DD format

impressions integer Number of impressions

clicks integer Number of clicks

spend float Advertising cost (USD)

conversions integer Number of conversions

create lại bảng cho khớp với kiểu dữ liệu đề cho như này
```

## SQL đã sử dụng

```sql
CREATE TABLE campaign (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id VARCHAR(50),
    report_date DATE,
    impressions INT,
    clicks INT,
    spend DECIMAL(15,2),
    conversions INT
);
```

---

# 2. Convert bảng cũ sang bảng chuẩn hơn

## Prompt

```text
CREATE TABLE flinters_test (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id VARCHAR(20),
    date DATE,
    impressions INT,
    clicks INT,
    spend DOUBLE,
    conversions INT
);

convert về bảng này nếu bảng trên không hợp lí
```

## SQL migrate

```sql
INSERT INTO campaign (
    campaign_id,
    report_date,
    impressions,
    clicks,
    spend,
    conversions
)
SELECT
    campaign_id,
    date,
    impressions,
    clicks,
    CAST(spend AS DECIMAL(15,2)),
    conversions
FROM flinters_test;
```

---

# 3. Tạo bảng summary

## Prompt

```text
đã tối ưu truy vấn, hiện tại truy vấn còn 17s để xuất ra file sum của 50 bản ghi theo campaign_id tính tổng các trường trên.
cần lưu data này vào 1 bảng tạm rồi từ đấy lấy giá trị theo yêu cầu để xuất ra 2 bản ghi.
viết các bước thực hiện lưu data vào bảng tạm và 2 hàm export tương ứng cho 2 file trên
```

## SQL create summary table

```sql
CREATE TABLE campaign_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    campaign_id VARCHAR(50),

    total_impressions BIGINT,

    total_clicks BIGINT,

    total_spend DECIMAL(20,2),

    total_conversions BIGINT,

    ctr DECIMAL(20,6),

    cpa DECIMAL(20,6),

    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

# 4. Công thức business

## Prompt

```text
CTR = total_clicks / total_impressions
CPA = total_spend / total_conversions
If conversions = 0, ignore or return null for CPA
viết lại hàm insert
```

## Formula

CTR

```text
CTR = total_clicks / total_impressions
```

CPA

```text
CPA = total_spend / total_conversions
```

Rule:

```text
If conversions = 0 -> CPA = NULL
```

---

# 5. Aggregate dữ liệu sang campaign_summary

## Prompt

```text
hiện tại đã tạo 1 bảng campaign_summary, viết sql insert vào bảng này các thông tin trên
```

## SQL aggregate

```sql
INSERT INTO campaign_summary (
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

    SUM(impressions),

    SUM(clicks),

    SUM(spend),

    SUM(conversions),

    ROUND(
        SUM(clicks) / NULLIF(SUM(impressions), 0),
        6
    ) AS ctr,

    CASE
        WHEN SUM(conversions) = 0 THEN NULL
        ELSE ROUND(
            SUM(spend) / SUM(conversions),
            6
        )
    END AS cpa

FROM campaign
GROUP BY campaign_id;
```

---

# 6. Tạo Entity cho bảng campaign

## Prompt

```text
create class entity cho bảng này
```

## Entity

```java
@Entity
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String campaignId;

    private LocalDate reportDate;

    private Integer impressions;

    private Integer clicks;

    private BigDecimal spend;

    private Integer conversions;
}
```

---

# 7. Tạo Entity cho bảng campaign_summary

## Prompt

```text
hiện tại đã tạo 1 bảng campaign_summary

giờ tạo class trên project tương ứng với 2 bảng,
1 bảng campaign,
1 bảng campaign_summary
```

## Entity

```java
@Entity
@Table(name = "campaign_summary")
public class CampaignSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String campaignId;

    private Long totalImpressions;

    private Long totalClicks;

    private BigDecimal totalSpend;

    private Long totalConversions;

    private BigDecimal ctr;

    private BigDecimal cpa;
}
```

---

# 8. Repository query top 10 CTR/CPA

## Prompt

```text
trong file repo của campaignSummary,
đặt tên 2 function cho việc lấy top 10 ctr và cpa
```

## Prompt tiếp theo

```text
Top 10 campaigns with the highest CTR
Top 10 campaigns with the lowest CPA

viết lại tên hàm đúng chuẩn
```

## Repository

```java
public interface CampaignSummaryRepository
        extends JpaRepository<CampaignSummary, Long> {

    @Query(value =
            "SELECT * " +
            "FROM campaign_summary " +
            "ORDER BY ctr DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<CampaignSummary>
    findTop10CampaignsByHighestCtr();


    @Query(value =
            "SELECT * " +
            "FROM campaign_summary " +
            "WHERE cpa IS NOT NULL " +
            "ORDER BY cpa ASC " +
            "LIMIT 10",
            nativeQuery = true)
    List<CampaignSummary>
    findTop10CampaignsByLowestCpa();
}
```

---

# 9. Export CSV

## Prompt

```text
đã xuất được 2 list cần tìm.
viết method export 2 list vừa rồi ra file .csv
```

## Dependency

```xml
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.9</version>
</dependency>
```

## Utility export CSV

```java
public class CsvExportUtil {

    public static void exportCampaignSummaryCsv(
            List<CampaignSummary> data,
            String fileName
    ) {

        try (
                CSVWriter writer =
                        new CSVWriter(new FileWriter(fileName))
        ) {

            String[] header = {
                    "campaign_id",
                    "total_impressions",
                    "total_clicks",
                    "total_spend",
                    "total_conversions",
                    "ctr",
                    "cpa"
            };

            writer.writeNext(header);

            for (CampaignSummary item : data) {

                String[] row = {
                        item.getCampaignId(),
                        value(item.getTotalImpressions()),
                        value(item.getTotalClicks()),
                        value(item.getTotalSpend()),
                        value(item.getTotalConversions()),
                        value(item.getCtr()),
                        value(item.getCpa())
                };

                writer.writeNext(row);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
```

---

# 10. Export 2 file CSV

## Prompt

```text
nó xuất ra file lưu vào thư mục nào ?
tên file đang được xuất ra là gì
```

## Export code

```java
CsvExportUtil.exportCampaignSummaryCsv(
        top10Ctr,
        "top10_ctr.csv"
);

CsvExportUtil.exportCampaignSummaryCsv(
        top10Cpa,
        "top10_cpa.csv"
);
```

## Output

```text
project-root/
 ├── top10_ctr.csv
 ├── top10_cpa.csv
```

---

# 11. Chạy project Spring Boot

## Prompt

```text
org.apache.hc.client5.http.HttpHostConnectException:
Connect to http://localhost:9200 failed
```

## Prompt tiếp theo

```text
RedisConnectionFailureException:
Unable to connect to Redis
```

## Root cause

```text
Elasticsearch chưa chạy
Redis chưa chạy
```

---

# 12. Disable Redis / Elasticsearch nếu không dùng

## application.yml

```yaml
management:
  health:
    elasticsearch:
      enabled: false

    redis:
      enabled: false
```

---

# 13. Chạy project

## Maven

```bash
mvn spring-boot:run
```

## IntelliJ IDEA

```text
Run Application.main()
```

---

# 14. Kết quả cuối cùng

```text
1. Import CSV vào MariaDB
2. Aggregate dữ liệu campaign
3. Lưu vào campaign_summary
4. Query top 10 CTR
5. Query top 10 CPA
6. Export ra:
   - top10_ctr.csv
   - top10_cpa.csv
```


############################################


## 1. Cài Docker Desktop và xử lý lỗi WSL

### Prompt
```text
Manifest extraction failed: Could not find file 'DockerDesktop.d4w'
```

### Nội dung đã xử lý
- Xóa thư mục temp của Docker Desktop
- Download lại installer
- Run as Administrator
- Kiểm tra WSL2
- Update WSL bằng:

```powershell
wsl --update
```

---

## 2. Khởi tạo MariaDB container bằng Docker

### Prompt
```text
đã chạy xong docker, giờ cần tạo 1 container để tạo database và bảng, sau đó import dữ liệu từ file csv vào bảng
```

### Docker run MariaDB

```bash
docker run -d \
  --name mariadb-local \
  -e MARIADB_ROOT_PASSWORD=123456 \
  -e MARIADB_DATABASE=testdb \
  -p 3306:3306 \
  mariadb:11
```

---

## 3. Connect DataGrip tới MariaDB container

### Prompt
```text
giờ đã có container mariadb, connect từ datagrip r vào tạo db tạo bảng sau
```

### Connection info

```text
Host: localhost
Port: 3306
Username: root
Password: 123456
```

---

## 4. Tạo database

### SQL

```sql
CREATE DATABASE demo;
```

---

## 5. Tạo bảng campaign_report

### SQL

```sql
CREATE TABLE campaign_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id VARCHAR(50),
    report_date DATE,
    impressions BIGINT,
    clicks BIGINT,
    spend DECIMAL(18,2),
    conversions BIGINT
);
```

---

## 6. Import dữ liệu CSV vào MariaDB

### Prompt
```text
import dữ liệu từ file csv vào bảng
```

### SQL

```sql
LOAD DATA LOCAL INFILE '/data/campaign.csv'
INTO TABLE campaign_report
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(
    campaign_id,
    report_date,
    impressions,
    clicks,
    spend,
    conversions
);
```

---

## 7. Requirement analytics

### Prompt
```text
For each campaign_id, compute:

total_impressions
total_clicks
total_spend
total_conversions
CTR = total_clicks / total_impressions
CPA = total_spend / total_conversions
If conversions = 0, ignore or return null for CPA
```

---

## 8. Query aggregate theo campaign_id

### SQL

```sql
SELECT
    campaign_id,

    SUM(impressions) AS total_impressions,
    SUM(clicks) AS total_clicks,
    SUM(spend) AS total_spend,
    SUM(conversions) AS total_conversions,

    ROUND(
        CAST(SUM(clicks) AS DECIMAL(20,6))
        / NULLIF(SUM(impressions), 0),
        6
    ) AS CTR,

    ROUND(
        SUM(spend)
        / NULLIF(SUM(conversions), 0),
        6
    ) AS CPA

FROM campaign_report
GROUP BY campaign_id;
```

---

## 9. Tối ưu query cho bảng 26 triệu records

### Prompt
```text
đánh index xong thì query vẫn đang chạy dù giờ đã hơn 3minutes rồi
```

### Phân tích execution plan

```sql
EXPLAIN
SELECT
    campaign_id,
    SUM(impressions),
    SUM(clicks),
    SUM(spend),
    SUM(conversions)
FROM campaign_report
GROUP BY campaign_id;
```

### Kết quả gặp phải

```text
type = ALL
key = NULL
```

=> MariaDB đang full table scan.

---

## 10. Tạo covering index

### SQL

```sql
CREATE INDEX idx_campaign_cover
ON campaign_report (
    campaign_id,
    impressions,
    clicks,
    spend,
    conversions
);
```

### Analyze statistics

```sql
ANALYZE TABLE campaign_report;
```

---

## 11. Query tối ưu dùng FORCE INDEX

### SQL

```sql
SELECT
    campaign_id,
    SUM(impressions) total_impressions,
    SUM(clicks) total_clicks,
    SUM(spend) total_spend,
    SUM(conversions) total_conversions
FROM campaign_report FORCE INDEX(idx_campaign_cover)
GROUP BY campaign_id;
```

---

## 12. Tách aggregate và metric calculation

### Prompt
```text
có nên lưu kết quả select 5 cột kia vào 1 bảng tạm, sau đó tính ctr cpa thì lấy từ cột đấy ra không
```

### Tạo temporary summary table

```sql
CREATE TEMPORARY TABLE tmp_campaign_summary AS
SELECT
    campaign_id,
    SUM(impressions) AS total_impressions,
    SUM(clicks) AS total_clicks,
    SUM(spend) AS total_spend,
    SUM(conversions) AS total_conversions
FROM campaign_report FORCE INDEX(idx_campaign_cover)
GROUP BY campaign_id;
```

### Tính CTR/CPA từ bảng tạm

```sql
SELECT
    campaign_id,
    total_impressions,
    total_clicks,
    total_spend,
    total_conversions,

    total_clicks / NULLIF(total_impressions, 0) AS CTR,

    total_spend / NULLIF(total_conversions, 0) AS CPA

FROM tmp_campaign_summary;
```


