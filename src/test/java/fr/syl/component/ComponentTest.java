package fr.syl.component;

import fr.syl.AppConfig;
import fr.syl.controller.CensusRestController;
import fr.syl.exception.FormatException;
import fr.syl.model.CensusResult;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore  // comment this to run this small component test. DB needs to be configured in test/application.properties
@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@SpringBootTest(classes = {AppConfig.class, CensusRestController.class })
@ComponentScan( basePackages = "fr.syl" )
public class ComponentTest {
    @Autowired
    private CensusRestController controller;

    @Test
    public void testGetAverage() throws FormatException {
        List<CensusResult> expected = Arrays.asList(
            new CensusResult("Female", 103984, 35.61822972765041),
            new CensusResult("Male", 95539, 33.27081087304661)
        );
        List<CensusResult> result = controller.getAverage("sex", 0);
        assertEquals(expected, result);
    }

    @Test
    public void testGetAverageOffset() throws FormatException {
        List<CensusResult> expected = Collections.singletonList(
                new CensusResult("Male", 95539, 33.27081087304661)
        );
        List<CensusResult> result = controller.getAverage("sex", 1);
        assertEquals(expected, result);
    }

    @Test(expected = FormatException.class)
    public void testGetAverageNotFound() throws FormatException {
        controller.getAverage("unknown", 0);
    }
}
