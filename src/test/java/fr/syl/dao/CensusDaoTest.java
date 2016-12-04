package fr.syl.dao;

import com.google.common.collect.ImmutableSet;
import fr.syl.AppConfig;
import fr.syl.exception.FormatException;
import fr.syl.model.CensusResult;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CensusDaoTest {
    private static final String BOBBY_TABLES = "Robert'); DROP TABLE Students;--";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String COLUMN_TO_AVERAGE = "COLUMN_TO_AVERAGE";
    private static final String COLUMN_PREFIX = "COLUMN_";
    private static final String ORDER_BY = "ORDER_BY";
    private static final int NB_ENTRIES = 100;
    private static final int NB_COLUMNS = 10;

    @Mock
    private AppConfig config;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private ResultSet resultSet;
    @Mock
    private ResultSetMetaData metadata;

    @Captor
    private ArgumentCaptor<ResultSetExtractor<ImmutableSet<String>>> rseCaptor;

    private ImmutableSet<String> columns;

    @Spy
    @InjectMocks
    private CensusDao censusDao;

    @Before
    public void init() throws SQLException {
        MockitoAnnotations.initMocks(this);

        when(config.getTableName()).thenReturn(TABLE_NAME);
        when(config.getColumnToAverage()).thenReturn(COLUMN_TO_AVERAGE);
        when(config.getOrderBy()).thenReturn(ORDER_BY);
        when(config.getNbEntries()).thenReturn(NB_ENTRIES);

        when(resultSet.getMetaData()).thenReturn(metadata);
        when(metadata.getColumnCount()).thenReturn(NB_COLUMNS);

        List<String> columns = new ArrayList<>();
        for (int i = 0; i < NB_COLUMNS; ++i) {
            String column = COLUMN_PREFIX + i;
            columns.add(column);
            when(metadata.getColumnName(i + 1)).thenReturn(column);
        }
        columns.add(BOBBY_TABLES);
        this.columns = ImmutableSet.copyOf(columns);
    }

    @Test
    public void testGetCount() {
        int expected = 10;
        when(jdbcTemplate.queryForObject(String.format("select count(*) from `%s`", TABLE_NAME), Integer.class)).thenReturn(expected);
        assertEquals(expected, censusDao.getCount());
    }

    @Test
    public void testGetColumns() throws SQLException {
        censusDao.getColumns();
        ImmutableSet<String> result = getColumns();
        assertEquals(NB_COLUMNS, result.size());
    }

    @Test
    public void testGetColumnsCache() throws SQLException {
        prepareForGetColumns();
        censusDao.getColumns();
        censusDao.getColumns();
        // verify will check that query is only called once.
        ImmutableSet<String> result = getColumns();
        assertEquals(NB_COLUMNS, result.size());
    }

    @Test
    public void testGetColumnsWithoutAverage() throws SQLException {
        when(metadata.getColumnName(5)).thenReturn(COLUMN_TO_AVERAGE);
        censusDao.getColumns();
        ImmutableSet<String> result = getColumns();
        assertEquals(NB_COLUMNS - 1, result.size());
        assertFalse(result.contains(COLUMN_TO_AVERAGE));
    }

    @Test
    public void testGetAverage() throws FormatException {
        String column = COLUMN_PREFIX + 1;
        int offset = 99;

        doReturn(columns).when(censusDao).getColumns();
        censusDao.getAverage(column, offset);

        verify(jdbcTemplate).query(eq(String.format(
                "SELECT `%s` AS value, COUNT(*) AS count, AVG(%s) AS average FROM `%s`WHERE value IS NOT NULL AND `%s` IS NOT NULL GROUP BY value ORDER BY %s DESC LIMIT %d OFFSET %d",
                column, COLUMN_TO_AVERAGE, TABLE_NAME, COLUMN_TO_AVERAGE, ORDER_BY, NB_ENTRIES, offset)),
                Matchers.<BeanPropertyRowMapper<CensusResult>>any());
    }

    /**
     * query should never be called with unchecked params, but I wasn't able to call query with '?' parameter, I wonder if it's due to SQLite driver.
     * @throws FormatException
     */
    @Test
    @Ignore
    public void testGetAverageInjection() throws FormatException {
        int offset = 99;

        doReturn(columns).when(censusDao).getColumns();
        censusDao.getAverage(BOBBY_TABLES, offset);

        verify(jdbcTemplate, never()).query(eq(String.format(
                "SELECT `%s` AS value, COUNT(*) AS count, AVG(%s) AS average FROM `%s`WHERE value IS NOT NULL AND `%s` IS NOT NULL GROUP BY value ORDER BY %s DESC LIMIT %d OFFSET %d",
                BOBBY_TABLES, COLUMN_TO_AVERAGE, TABLE_NAME, COLUMN_TO_AVERAGE, ORDER_BY, NB_ENTRIES, offset)),
                Matchers.<BeanPropertyRowMapper<CensusResult>>any());
    }

    private void prepareForGetColumns() {
        when(jdbcTemplate.query(eq(String.format("SELECT * FROM `%s` LIMIT 1", TABLE_NAME)),
                Matchers.<ResultSetExtractor<ImmutableSet<String>>>any())).thenReturn(ImmutableSet.of());
    }

    private ImmutableSet<String> getColumns() throws SQLException {
        verify(jdbcTemplate).query(eq(String.format("SELECT * FROM `%s` LIMIT 1", TABLE_NAME)), rseCaptor.capture());
        ResultSetExtractor<ImmutableSet<String>> rse = rseCaptor.getValue();
        return rse.extractData(resultSet);
    }
}
