package service;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * A basic service.
 */
@ApplicationScoped
public class BasicServiceImpl implements BasicService {

    private static final Logger LOGGER = Logger.getLogger(BasicServiceImpl.class.getCanonicalName());

    @Override
    public void doWork() {
        LOGGER.info("Doing service");
    }
}
