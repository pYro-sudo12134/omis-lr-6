package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.service.base.AnalysisStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StatAnalysisStrategy implements AnalysisStrategy {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void analyze(List<String> data) {
        LOG.info(String.format("%s был вызван!", getClass()));
    }
}
