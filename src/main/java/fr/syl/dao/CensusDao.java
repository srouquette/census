package fr.syl.dao;

import com.google.common.collect.ImmutableSet;
import fr.syl.AppConfig;
import fr.syl.exception.FormatException;
import fr.syl.model.CensusResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CensusDao {
    private ImmutableSet<String> columns;

    @Autowired
    private AppConfig config;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * @return an immutable set of the column names
     */
    public ImmutableSet<String> getColumns() {
        if (columns == null) {
            columns = jdbcTemplate.query(String.format("SELECT * FROM `%s` LIMIT 1", config.getTableName()),
                    rs -> {
                        Set<String> result = new HashSet<>();
                        ResultSetMetaData metadata = rs.getMetaData();
                        for (int i = 1; i <= metadata.getColumnCount(); ++i) {
                            String column = metadata.getColumnName(i);
                            if (!config.getColumnToAverage().equals(column)) {
                                result.add(column);
                            }
                        }
                        return ImmutableSet.copyOf(result);
                    });
        }
        return columns;
    }

    public int getCount() {
        return jdbcTemplate.queryForObject(String.format("select count(*) from `%s`", config.getTableName()), Integer.class);
    }

    /**
     * @param column column to group the result by
     * @param offset offset to start
     * @return a list of {@link CensusResult} containing the average age based on the column used to group the elements
     * @throws FormatException
     */
    public List<CensusResult> getAverage(String column, int offset) throws FormatException {
        if (!getColumns().contains(column)) {
            throw new FormatException("column not found");
        }
        return jdbcTemplate.query(String.format(
                "SELECT `%s` AS value, COUNT(*) AS count, AVG(%s) AS average FROM `%s`WHERE value IS NOT NULL AND `%s` IS NOT NULL GROUP BY value ORDER BY %s DESC LIMIT %d OFFSET %d",
                column, config.getColumnToAverage(), config.getTableName(), config.getColumnToAverage(), config.getOrderBy(), config.getNbEntries(), offset),
                new BeanPropertyRowMapper<>(CensusResult.class));
    }

    /**
     * named parameters don't seem to work with SQLite driver...
     * I also tested it with "?"
     * But we can see that by using a SqlParameterSource, the query gets replaced by "?", and still fails with an Exception.
     * @param column column to group the result by
     * @param offset offset to start
     * @return a list of {@link CensusResult} containing the average age based on the column used to group the elements
     * @throws FormatException
     */
    public List<CensusResult> getAverageWithNamedParameters(String column, int offset) throws FormatException {
        if (!getColumns().contains(column)) {
            throw new FormatException("column not found");
        }
        SqlParameterSource parameters = new GetAverageSqlParameterSource(config, column, offset);
        return namedJdbcTemplate.query(
                "SELECT :column AS value, COUNT(*) AS count, AVG(:columnToAverage) AS average FROM :tableName WHERE value IS NOT NULL AND :columnToAverage IS NOT NULL GROUP BY value ORDER BY :orderBy DESC LIMIT :nbEntries OFFSET :offset",
                parameters,
                new BeanPropertyRowMapper<>(CensusResult.class));
    }

    private static class GetAverageSqlParameterSource extends AbstractSqlParameterSource {
        private final SqlParameterSource configParameters;
        private final SqlParameterSource inputParameters;

        GetAverageSqlParameterSource(AppConfig config, String column, int offset) {
            configParameters = new BeanPropertySqlParameterSource(config);
            inputParameters  = new MapSqlParameterSource() {{
                addValue("column", column);
                addValue("offset", offset);
            }};
        }

        @Override
        public boolean hasValue(String paramName) {
            return configParameters.hasValue(paramName) || inputParameters.hasValue(paramName);
        }

        @Override
        public Object getValue(String paramName) throws IllegalArgumentException {
            return configParameters.hasValue(paramName)  ? configParameters.getValue(paramName) : inputParameters.getValue(paramName);
        }

        @Override
        public int getSqlType(String paramName) throws IllegalArgumentException {
            return configParameters.hasValue(paramName)  ? configParameters.getSqlType(paramName) : inputParameters.getSqlType(paramName);
        }
    }
}
