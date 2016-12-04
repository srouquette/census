package fr.syl.model;

import fr.syl.exception.CensusConfigException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CensusConfigTest {
    /**
     * just freezing the api
     */
    @Test
    public void testPublicMethods() {
        assertEquals(4, CensusConfig.class.getDeclaredMethods().length);
    }

    @Test(expected = CensusConfigException.class)
    public void testConstructor() throws CensusConfigException {
        new CensusConfig(null, 100);
    }

    @Test
    public void testEquals() throws CensusConfigException {
        CensusConfig censusConfig = new CensusConfig("age", 100);
        assertEquals(censusConfig, censusConfig);
        assertEquals(censusConfig, new CensusConfig("age", 100));
        assertNotEquals(censusConfig, new CensusConfig("weight", 100));
        assertNotEquals(censusConfig, new CensusConfig("age", 99));
    }
}
