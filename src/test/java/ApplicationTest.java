import com.example.javacore_springboot.Application;
import com.example.javacore_springboot.entity.CampaignSummary;
import com.example.javacore_springboot.repository.CampaignSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    @Mock
    private CampaignSummaryRepository repo;

    @InjectMocks
    private Application application;

    @Test
    void testExportTop10ByHighestCtr() {

        when(repo.findTop10ByOrderByCtrDesc())
                .thenReturn(List.of(new CampaignSummary()));

        List<CampaignSummary> result =
                application.exportTop10ByHighestCtr();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(repo, times(1)).findTop10ByOrderByCtrDesc();
    }

    @Test
    void testExportTop10ByLowestCpa() {

        when(repo.findTop10ByCpaIsNotNullOrderByCpaAsc())
                .thenReturn(List.of(new CampaignSummary()));

        List<CampaignSummary> result =
                application.exportTop10ByLowestCpa();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(repo, times(1))
                .findTop10ByCpaIsNotNullOrderByCpaAsc();
    }

    @Test
    void testCtrEmpty() {

        when(repo.findTop10ByOrderByCtrDesc())
                .thenReturn(List.of());

        List<CampaignSummary> result =
                application.exportTop10ByHighestCtr();

        assertTrue(result.isEmpty());
    }

    @Test
    void testCpaEmpty() {

        when(repo.findTop10ByCpaIsNotNullOrderByCpaAsc())
                .thenReturn(List.of());

        List<CampaignSummary> result =
                application.exportTop10ByLowestCpa();

        assertTrue(result.isEmpty());
    }
}