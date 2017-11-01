package web;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * The controller for the AutoStore Picking screen.
 */
@RequestScoped
@Named("timerController")
public class TimerController {
    private static final Logger LOGGER = Logger.getLogger(TimerController.class.getCanonicalName());

    final String controllerName = "Timer Controller";

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("timerController - Request scoped bean constructed. ");

    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.info("timerController - Request scoped bean destroyed. ");
    }

    public String getControllerName() {
        return controllerName;
    }

}