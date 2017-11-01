package jmx;

import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jmx Listener - that cares about knowing when the deployment WAR is put to run:
 * jboss.as:deployment=wm6-powerhousejumpstart-war.war.
 *
 * <P>
 * We make this class an app scoped bean, even though jmx listener would typically be a plain pojo.
 */
@ApplicationScoped
public class WildflyAppDeploymentMbeanListener implements NotificationListener, NotificationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WildflyAppDeploymentMbeanListener.class);

    private static final String WAR_NAME_PATTERN_STR = "jboss.as:deployment=(.*)";
    private static final Pattern WAR_NAME_PATTERN = Pattern.compile(WAR_NAME_PATTERN_STR);

    /**
     * On wildfly, when we bind to a jmx bean corresponding to application deployment (e.g.
     * jboss.as:deployment=wm6-powerhousejumpstart-war.war) we will get an event notification of notification type will
     * be this string and it will signify that the WAR is now fully deployed.
     */
    private static final String WILDFLY_EXPECTED_NOTIFICATION_TYPE = "deployment-deployed";

    // Listen for the event

    /**
     * Get the name of the deployed application
     *
     * @param notification
     *            the jmx notification
     * @return A string with the name of the deployed war
     */
    protected String getDeploymentName(Notification notification) {
        String jmxObjectName = notification.getSource().toString();
        java.util.regex.Matcher matcher = WAR_NAME_PATTERN.matcher(jmxObjectName);
        if (!matcher.matches()) {
            LOGGER.error("The object name {} does not match our pattern {} ", jmxObjectName, WAR_NAME_PATTERN_STR);
            return "";
        }
        return matcher.group(1);
    }

    /**
     * Determine if the notification is interesting for our notification listener or not.
     *
     * @param notification
     *            The JMX notification that can tell us if a deployment even is coming
     * @return TRUE if the notification tells us the deployment is finished, false otherwise.
     */
    @Override
    public boolean isNotificationEnabled(Notification notification) {
        String notificationType = notification.getType();
        LOGGER.trace("checking if notification of type: {} is relevant ", notificationType);
        return notification.getType().equals(WILDFLY_EXPECTED_NOTIFICATION_TYPE);
    }

    @Override
    public void handleNotification(Notification notification, Object arg1) {
        String warName = getDeploymentName(notification);
        LOGGER.info("\n\n\n IMPORTANT NOTIFICATION: {} . WE KNOW THAT THE APPLICATION {} IS DEPLOYED. "
                + "\n\n But it was necessary to hard code the managed bean name "
                + "\n\n We do not want to be hard coding WAR names to do our registration."
                + "\n\n Ideally we want to query and find the app that is suitable. ", notification, warName);

    }

}
