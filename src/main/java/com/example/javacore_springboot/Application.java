package com.example.javacore_springboot;

import com.example.javacore_springboot.Utils.CsvExportUtil;
import com.example.javacore_springboot.entity.CampaignSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.javacore_springboot.repository.CampaignSummaryRepository;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private CampaignSummaryRepository repo;


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {

		List<CampaignSummary> ctrTop10 = exportTop10ByHighestCtr();
		List<CampaignSummary> cpaTop10 = exportTop10ByLowestCpa();

		if (ctrTop10 == null || ctrTop10.isEmpty()) throw new IllegalStateException("CTR empty");
		if (cpaTop10 == null || cpaTop10.isEmpty()) throw new IllegalStateException("CPA empty");

		System.out.println("hungd27 check:" + ctrTop10);
		System.out.println("hungd27 check:" + cpaTop10);

		CsvExportUtil.exportCampaignSummaryCsv(
				ctrTop10,
				"top10_ctr.csv"
		);

		CsvExportUtil.exportCampaignSummaryCsv(
				cpaTop10,
				"top10_cpa.csv"
		);

	}
	public List<CampaignSummary> exportTop10ByHighestCtr(){
		return repo.findTop10ByOrderByCtrDesc();
	}
	public List<CampaignSummary> exportTop10ByLowestCpa(){
		return repo.findTop10ByCpaIsNotNullOrderByCpaAsc();
	}
}
