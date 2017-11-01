package interceptor;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.inject.Vetoed;
import javax.interceptor.InvocationContext;

/**
 * Interceptor meant to be used around post constructs to log the exceptions if they take place during construction of a
 * bean.
 *
 * <a href="https://issues.jboss.org/browse/WELD-2138">We need to veto an interceptor - so that it is not treated as
 * managed bean by WELD.</a>
 */
@Vetoed
@Priority(1)
public class PostConstructInterceptor implements Serializable {

    /**
     * Default constructor needed by the container to produce interceptors
     */
    public PostConstructInterceptor() {
    }

    /**
     * The intercepting method. It assigns a log context, adds log messages around the call and translates exceptions.
     *
     * @param invocation
     *            The invocation context within the method is called
     * @throws Exception
     *             InvocationContext.proceed() throws Exception
     */
    @PostConstruct
    public void intercept(InvocationContext context) throws Exception {
        Logger logger = Logger.getLogger(context.getTarget().getClass().getCanonicalName());
        logger.info("Post construct interceptor - intercepting class: "
                + context.getTarget().getClass().getCanonicalName());
        Exception unexpectedError = null;
        try {
            context.proceed();
        } catch (Exception e) {
            // we log the message because Weblogic tends to swallow post construct exceptions
            String errMsg = String.format("Unexpected error in method intercepted took place: %1%s", e.getMessage());
            logger.log(Level.SEVERE, errMsg, e);

            // fire the exception up
            throw new RuntimeException(errMsg, e);
        } finally {
            logger.info("Post construct interceptor - finished intercepting class: "
                    + context.getTarget().getClass().getCanonicalName());
        }

    }

}
