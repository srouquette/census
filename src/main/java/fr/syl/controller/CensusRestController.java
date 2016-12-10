package fr.syl.controller;

import fr.syl.AppConfig;
import fr.syl.dao.CensusDao;
import fr.syl.exception.CensusConfigException;
import fr.syl.exception.FormatException;
import fr.syl.model.CensusConfig;
import fr.syl.model.CensusResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * endpoint to retrieve census information
 */
@RestController
@RequestMapping("/census")
public class CensusRestController {

    @Autowired
    private CensusDao censusDao;
    @Autowired
    private AppConfig config;

    @RequestMapping(path="/columns")
    public Set<String> getColums() {
        return censusDao.getColumns();
    }

    @RequestMapping(path="/count")
    public int getCount() {
        return censusDao.getCount();
    }

    @RequestMapping(path="/average/{column}")
    public List<CensusResult> getAverage(@PathVariable @ValidColumn String column,
                                         @RequestParam(defaultValue = "0") @ValidOffset Integer offset) throws FormatException {
        return censusDao.getAverage(column, offset);
        //return censusDao.getAverageWithNamedParameters(column, offset);
    }

    @RequestMapping(path="/config")
    public CensusConfig getConfig() throws CensusConfigException {
        return new CensusConfig(config.getColumnToAverage(), config.getNbEntries());
    }
}
