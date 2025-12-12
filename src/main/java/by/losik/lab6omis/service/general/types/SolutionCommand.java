package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.service.base.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolutionCommand implements Command {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void call() {
        LOG.info(String.format("%s был вызван!", getClass()));
    }

    @Override
    public void cancel() {
        LOG.info(String.format("%s был отменен!", getClass()));
    }
}
