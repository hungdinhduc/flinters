import com.example.javacore_springboot.Utils.CsvExportUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvExportUtilTest {

    @Test
    void shouldThrowWhenEnvMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            CsvExportUtil.exportCampaignSummaryCsv(List.of(), "test.csv");
        });
    }
}