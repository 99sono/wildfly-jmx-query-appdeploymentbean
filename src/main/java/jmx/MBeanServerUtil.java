package jmx;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.management.MBeanServer;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 *
 * A basic utility class to give us access to the MBeanserver.
 */
@ApplicationScoped
public class MBeanServerUtil {

    private static final QueryExp QUERY_EXPRESSION_NOT_NEEDED = null;

    /**
     *
     * @return The application server mbean server.
     */
    protected MBeanServer getMbeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * Hunt for all JMX objects that match the given object names.
     *
     * @param jmxQuery
     *            The jmx query
     * @return The jmx object names
     */
    public List<String> findAllObjectNames(String jmxQuery) {
        try {
            // (a) setup variables
            MBeanServer mBeanServer = getMbeanServer();
            ObjectName queryObject = new ObjectName(jmxQuery);
            // (b) let the jmx server run the query
            Set<ObjectName> objecNames = mBeanServer.queryNames(queryObject, QUERY_EXPRESSION_NOT_NEEDED);
            // (c) We return the object names as strings
            return objecNames.stream().map(curretObjectName -> curretObjectName.getCanonicalName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            String errMsg = String.format(
                    "Unexpected error took place while trying to search for all object names matching search query %1$s ",
                    jmxQuery);
            throw new RuntimeException(errMsg, e);
        }
    }

    /**
     * Register a JMX listener
     *
     * @param jmxObjectName
     *            The name of the application managed bean where we want to register our listner
     * @param listener
     *            The jmx listener
     * @param notificationFilter
     *            the jmx filter
     */
    public void registerManagedBeanListener(String jmxObjectName, NotificationListener listener,
            NotificationFilter notificationFilter) {
        try {
            // (a) setup variables
            MBeanServer mBeanServer = getMbeanServer();
            ObjectName appName = new ObjectName(jmxObjectName);
            // (b) let the jmx server run the query
            mBeanServer.addNotificationListener(appName, listener, notificationFilter, null);
        } catch (Exception e) {
            String errMsg = String.format("Unexpected error took place while trying to register a listener %1$s ",
                    jmxObjectName);
            throw new RuntimeException(errMsg, e);
        }
    }

    /**
     * Remote the notification listener
     * 
     * @param jmxObjectName
     *            the jmx object that has our listener attached to it
     * @param listener
     *            the listener we want to unregister
     */
    public void unregisterManagedBeanListener(String jmxObjectName, NotificationListener listener) {
        try {
            // (a) setup variables
            MBeanServer mBeanServer = getMbeanServer();
            ObjectName appName = new ObjectName(jmxObjectName);
            // (b) let the jmx server run the query
            mBeanServer.removeNotificationListener(appName, listener);
        } catch (Exception e) {
            String errMsg = String.format("Unexpected error took place while trying to un-register a listener %1$s ",
                    jmxObjectName);
            throw new RuntimeException(errMsg, e);
        }
    }
}
