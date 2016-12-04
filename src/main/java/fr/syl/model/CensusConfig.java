package fr.syl.model;

import fr.syl.exception.CensusConfigException;

/**
 * class used in the /config endpoint, to return a few information about the micro-service.
 * I don't serialize AppConfig because it may expose too much stuff.
 */
public class CensusConfig {
    private String columnToAverage;
    private int nbEntries;

    public CensusConfig(String columnToAverage, int nbEntries) throws CensusConfigException {
        if (columnToAverage == null) {
            throw new CensusConfigException("columnToAverage is null");
        }
        this.columnToAverage = columnToAverage;
        this.nbEntries = nbEntries;
    }

    public String getColumnToAverage() {
        return columnToAverage;
    }

    public int getNbEntries() {
        return nbEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o.getClass().equals(CensusConfig.class))) {
            return false;
        }
        CensusConfig other = (CensusConfig)o;
        return columnToAverage.equals(other.columnToAverage) && nbEntries == other.nbEntries;
    }

    @Override
    public int hashCode() {
        return columnToAverage.hashCode() + nbEntries;
    }
}
