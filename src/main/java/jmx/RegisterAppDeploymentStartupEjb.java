package jmx;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a basic EJB that will be triggered on startup to register a JMX lister on wildfly.
 */
@Startup
@LocalBean
@Singleton
public class RegisterAppDeploymentStartupEjb {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterAppDeploymentStartupEjb.class);

    String WILDFLY_QUERY_ALL_DEPLOYMENTS = "jboss.as:deployment=*";

    String HARD_CODED_MANAGED_BEAN_NAME = "jboss.as:deployment=wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war";

    @Inject
    MBeanServerUtil mbeanServerUtil;

    @Inject
    WildflyAppDeploymentMbeanListener wildflyAppDeploymentMbeanListener;

    @PostConstruct
    public void postConstruct() {
        // (a) Just log some marker information that our registration process is getting launched
        LOGGER.info("\n\n\n\nStaring the registration process of a jmx notification listener");

        // (b) Query the MBEan server for existing deployments
        // we will get no results at this point in time
        getArbitraryApplicationDeployment();

        // (c) Going to brute force managed bean registration
        bruteForceManagedBeanRegistration();

        LOGGER.info(
                "Registration process finished using brute force hard coded approach. We should seen after the application deployed message from wildfly our own messge coming from the listener. \n\n\n\n");

    }

    /**
     * Hard-code a listener to be registered against the app server managed bean we know that exists and that can send
     * us the application deployed notification. The problem is we could not quey the object name during deployment.
     */
    protected void bruteForceManagedBeanRegistration() {
        mbeanServerUtil.registerManagedBeanListener(HARD_CODED_MANAGED_BEAN_NAME, wildflyAppDeploymentMbeanListener,
                wildflyAppDeploymentMbeanListener);
    }

    /**
     * This our problem. We would hope that druing the Startup phase of our EJB, we would see the deployment management
     * for the ongoing deployment. IN fact, there is a managed bean that will fire a notification saying "deployed" but
     * we cannot find it with our query.
     *
     * @return Normally we will be returning the empty string, because our query will come with no results, otherwise we
     *         would return here the first deployment we find. IN our case we are hunting for a specific war deployment.
     */
    // FIXME:
    // We have no results for deployed applications - so we cannot dynamically determine
    // what application is currently actively getting deployed
    public String getArbitraryApplicationDeployment() {
        List<String> allDeployments = mbeanServerUtil.findAllObjectNames(WILDFLY_QUERY_ALL_DEPLOYMENTS);

        // (a) Determine if we have any result
        if (allDeployments.isEmpty()) {
            LOGGER.warn("No application deployment could be found.");
            return "";
        }
        // (b) We have a result
        LOGGER.info("We have found application deployments: {}. We are returning the first deployment. ",
                allDeployments);
        return allDeployments.get(0);
    }

    @PreDestroy
    public void preDestory() {
        LOGGER.info("\n\n\nGOING to unregister our listener from the managed bean: {} ", HARD_CODED_MANAGED_BEAN_NAME);
        mbeanServerUtil.unregisterManagedBeanListener(HARD_CODED_MANAGED_BEAN_NAME, wildflyAppDeploymentMbeanListener);
    }

}
