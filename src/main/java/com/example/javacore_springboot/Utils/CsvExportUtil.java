package com.example.javacore_springboot.Utils;
import com.example.javacore_springboot.entity.CampaignSummary;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.List;

public class CsvExportUtil {

    public static void exportCampaignSummaryCsv(
            List<CampaignSummary> data,
            String fileName
    ) {

        String exportDir = System.getenv("EXPORT_PATH");

        if (exportDir == null || exportDir.isBlank()) {
            throw new IllegalArgumentException("EXPORT_PATH is missing");
        }

        // tạo thư mục nếu chưa tồn tại
        File dir = new File(exportDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        // full path
        String filePath = exportDir + fileName;

        try (
                CSVWriter writer =
                        new CSVWriter(new FileWriter(filePath))
        ) {

            /**
             * Header
             */
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

            /**
             * Data
             */
            for (CampaignSummary item : data) {

                if (item == null) continue;

                String[] row = {

                        value(item.getCampaignId()),

                        value(item.getTotalImpressions()),

                        value(item.getTotalClicks()),

                        value(item.getTotalSpend()),

                        value(item.getTotalConversions()),

                        value(item.getCtr()),

                        value(item.getCpa())
                };

                writer.writeNext(row);
            }

            System.out.println(
                    "Exported file: " + fileName
            );

            System.out.println(
                    "Export path: " + filePath
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static String value(Object obj) {

        return obj == null
                ? ""
                : obj.toString();
    }
}
