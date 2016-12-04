package fr.syl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="app.config")
public class AppConfig {
    private String tableName;
    private String columnToAverage;
    private OrderByEnum orderBy;
    private int nbEntries;

    /**
     * @return table to retrieve the data
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return column name to average
     */
    public String getColumnToAverage() {
        return columnToAverage;
    }

    /**
     * @return how to sort the column (count or average)
     */
    public String getOrderBy() {
        return orderBy.toString();
    }

    /**
     * @return limit of the number of entries to return
     */
    public int getNbEntries() {
        return nbEntries;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumnToAverage(String column) {
        this.columnToAverage = column;
    }

    public void setOrderBy(OrderByEnum orderBy) {
        this.orderBy = orderBy;
    }

    public void setNbEntries(int nbEntries) {
        this.nbEntries = nbEntries;
    }

    private enum OrderByEnum {
        COUNT("count"),
        AVERAGE("average");

        private String value;

        OrderByEnum(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
