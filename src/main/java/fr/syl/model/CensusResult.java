package fr.syl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CensusResult {
    @JsonProperty
    private String value;
    @JsonProperty
    private Integer count;
    @JsonProperty
    private Double average;

    public CensusResult() {
    }

    public CensusResult(String value, Integer count, Double average) {
        this.value = value;
        this.count = count;
        this.average = average;
    }
    // setters are for BeanPropertyRowMapper, doesn't seem to do it with only a constructor.

    public void setValue(String value) {
        this.value = value;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return String.format("value: %s, count: %s, average: %s", value, count, average);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o.getClass().equals(CensusResult.class))) {
            return false;
        }
        CensusResult other = (CensusResult) o;
        return Objects.equals(value, other.value) &&
                Objects.equals(count, other.count) &&
                Objects.equals(average, other.average);
    }

    @Override
    public int hashCode() {
        return count + (value != null ? value.hashCode() : 0);
    }
}
