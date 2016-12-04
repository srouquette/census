package fr.syl.controller;

import com.google.common.collect.ImmutableSet;
import fr.syl.AppConfig;
import fr.syl.dao.CensusDao;
import fr.syl.exception.CensusConfigException;
import fr.syl.exception.FormatException;
import fr.syl.model.CensusConfig;
import fr.syl.model.CensusResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CensusRestControllerTest {
    @Mock
    private CensusDao censusDao;
    @Mock
    private AppConfig config;

    @InjectMocks
    private CensusRestController controller;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConfig() throws CensusConfigException {
        CensusConfig censusConfig = new CensusConfig("age", 100);
        when(config.getColumnToAverage()).thenReturn(censusConfig.getColumnToAverage());
        when(config.getNbEntries()).thenReturn(censusConfig.getNbEntries());

        CensusConfig result = controller.getConfig();
        assertEquals(censusConfig, result);
    }

    @Test
    public void testGetCount() {
        int expected = 99;
        when(censusDao.getCount()).thenReturn(expected);
        assertEquals(expected, controller.getCount());
    }

    @Test
    public void testGetColumns() {
        ImmutableSet<String> expected = ImmutableSet.of("test");
        when(censusDao.getColumns()).thenReturn(expected);
        assertEquals(expected, controller.getColums());
    }

    @Test
    public void testGetAverage() throws FormatException {
        String column = "column";
        int offset = 0;
        List<CensusResult> expected = Arrays.asList(new CensusResult(), new CensusResult());
        when(censusDao.getAverage(column, offset)).thenReturn(expected);
        assertEquals(expected, controller.getAverage(column, offset));
    }
}
