# Campaign Report Processing System

## Overview

This project processes large campaign CSV files using Java, Spring Boot, and MariaDB.

The system performs the following steps:

1. Import campaign data from CSV
2. Aggregate campaign statistics by `campaign_id`
3. Calculate:
   - CTR (Click Through Rate)
   - CPA (Cost Per Acquisition)
4. Store aggregated data into `campaign_summary`
5. Export:
   - Top 10 campaigns with highest CTR
   - Top 10 campaigns with lowest CPA

Generated output files:

- `top10_ctr.csv`
- `top10_cpa.csv`

---

# Technologies Used

## Backend

- Java 21
- Spring Boot
- Spring Data JPA

## Database

- MariaDB 11

## CSV Processing

- OpenCSV 5.9

## Containerization

- Docker
- Docker Compose

---

# Libraries Used

| Library | Purpose |
|---|---|
| Spring Boot | Backend framework |
| Spring Data JPA | ORM and database access |
| MariaDB JDBC Driver | Database connection |
| OpenCSV | CSV export |
| Lombok | Reduce boilerplate code |

---

# Database Tables

## campaign

Stores raw campaign data imported from CSV.

## campaign_summary

Stores aggregated campaign statistics.

---

# Metrics

## CTR

CTR = total_clicks / total_impressions

## CPA

CPA = total_spend / total_conversions

If `conversions = 0`, CPA is set to `NULL`.

---

# Setup Instructions

## Prerequisites

Install the following software:

- Java 21
- Maven
- Docker Desktop

---

# Project Structure

```text
project/
 ├── src/
 ├── Dockerfile
 ├── docker-compose.yml
 ├── pom.xml
 ├── output/
 └── README.md
````

---

# How to Run the Program

## Option 1 — Run with Docker Compose (Recommended)

### Step 1

Open terminal in the project folder.

### Step 2

Run:

```bash
docker compose up --build
```

The system will:

1. Build the Spring Boot application
2. Build Docker images
3. Start MariaDB container
4. Start Spring Boot container
5. Process campaign data
6. Export CSV result files

---

# Output Files

Generated CSV files:

```text
output/top10_ctr.csv
output/top10_cpa.csv
```



# Docker Commands

## Build and Run

```bash
docker compose up --build
```

## Run in Background

```bash
docker compose up -d
```

## Stop Containers

```bash
docker compose down
```

## Remove Containers and Volumes

```bash
docker compose down -v
```

--- chạy xong chương trình ra được file -->
docker cp campaign-report-app:/app/output ./output để lấy file về local
---

# Processing Flow

```text
CSV File
    ↓
MariaDB campaign table
    ↓
Aggregate campaign statistics
    ↓
campaign_summary table
    ↓
Query Top 10 CTR
Query Top 10 CPA
    ↓
Export CSV files
```

---

# Performance

## Processing Time for 1GB File

Approximate processing time:

```text
~17 seconds
```

Environment:

* Local machine
* Docker Compose
* MariaDB aggregation
* Indexed queries

---

# Peak Memory Usage

Approximate peak memory usage:

```text
512MB - 1GB
```

Depends on:

* JVM heap size
* CSV file size
* Docker memory allocation

