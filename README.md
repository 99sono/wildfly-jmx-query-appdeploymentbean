# Wildfly 10.1.0Final - MBeanServer - not returning ManagedBean jboss.as:deployment=* during deployment

## PROBLEM 1:
During deployment of a WAR application, when @Startup phase is reached and the startup business logic is invoked,
we if query the MBean server for the existing AppDeployments, we do not get the -war application that is current being deployed on Startup phase.
So this means, we cannot dynamically register a listener to the MBeanserver based on the output of the query.

## WORK-AROUND FOR PROBLEM 1:
If we know the name of the WAR file being deployed we can brute force the registration of our listener into the right managed bean.
This is illustrated in the following deployment log:



```
  ####2017-11-01 18:20:19,116 ThreadId:19 INFO  [logger: org.wildfly.extension.messaging-activemq] - WFLYMSGAMQ0002: Bound messaging object to jndi name java:jboss/MessageDrivenBeanConnectionFactory <LogContext:none> <MSC service thread 1-5>
####2017-11-01 18:20:19,117 ThreadId:18 INFO  [logger: org.jboss.as.connector.deployment] - WFLYJCA0002: Bound JCA ConnectionFactory [java:/jms/JcomJmsFactory] <LogContext:none> <MSC service thread 1-4>
####2017-11-01 18:20:19,117 ThreadId:18 INFO  [logger: org.wildfly.extension.messaging-activemq] - WFLYMSGAMQ0002: Bound messaging object to jndi name java:jboss/exported/jms/JcomJmsFactory <LogContext:none> <MSC service thread 1-4>
####2017-11-01 18:20:19,140 ThreadId:22 INFO  [logger: org.apache.activemq.artemis.ra] - Resource adaptor started <LogContext:none> <MSC service thread 1-8>
####2017-11-01 18:20:19,140 ThreadId:21 INFO  [logger: org.jboss.weld.deployer] - WFLYWELD0003: Processing weld deployment wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war <LogContext:none> <MSC service thread 1-7>
####2017-11-01 18:20:19,141 ThreadId:22 INFO  [logger: org.jboss.as.connector.services.resourceadapters.ResourceAdapterActivatorService$ResourceAdapterActivator] - IJ020002: Deployed: file://RaActivatorNotificationConnectionFactory <LogContext:none> <MSC service thread 1-8>
####2017-11-01 18:20:19,141 ThreadId:22 INFO  [logger: org.jboss.as.connector.deployment] - WFLYJCA0002: Bound JCA ConnectionFactory [java:/jms/NotificationConnectionFactory] <LogContext:none> <MSC service thread 1-8>
####2017-11-01 18:20:19,141 ThreadId:22 INFO  [logger: org.wildfly.extension.messaging-activemq] - WFLYMSGAMQ0002: Bound messaging object to jndi name java:jboss/exported/jms/NotificationConnectionFactory <LogContext:none> <MSC service thread 1-8>
####2017-11-01 18:20:19,203 ThreadId:21 INFO  [logger: org.hibernate.validator.internal.util.Version] - HV000001: Hibernate Validator 5.2.4.Final <LogContext:none> <MSC service thread 1-7>
####2017-11-01 18:20:19,270 ThreadId:21 INFO  [logger: org.jboss.as.ejb3.deployment] - WFLYEJB0473: JNDI bindings for session bean named 'RegisterAppDeploymentStartupEjb' in deployment unit 'deployment "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war"' are as follows:

	java:global/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb!jmx.RegisterAppDeploymentStartupEjb
	java:app/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb!jmx.RegisterAppDeploymentStartupEjb
	java:module/RegisterAppDeploymentStartupEjb!jmx.RegisterAppDeploymentStartupEjb
	java:global/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb
	java:app/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb
	java:module/RegisterAppDeploymentStartupEjb
 <LogContext:none> <MSC service thread 1-7>
####2017-11-01 18:20:19,573 ThreadId:17 INFO  [logger: org.jboss.weld.Version] - WELD-000900: 2.3.5 (Final) <LogContext:none> <MSC service thread 1-3>

------------------------------------------------ START 1:
####2017-11-01 18:20:20,203 ThreadId:130 INFO  [logger: jmx.RegisterAppDeploymentStartupEjb] - 



Staring the registration process of a jmx notification listener <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:20,231 ThreadId:130 INFO  [logger: jmx.RegisterAppDeploymentStartupEjb] - We have found application deployments: [jboss.as:deployment=wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war]. We are returning the first deployment.  <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:20,233 ThreadId:130 INFO  [logger: jmx.RegisterAppDeploymentStartupEjb] - Registration process finished using brute force hard coded approach. We should seen after the application deployed message from wildfly our own messge coming from the listener. 


------------------------------------------------ FINISH 1: (we had to register using a hard coded string)
 <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:20,357 ThreadId:130 INFO  [logger: javax.enterprise.resource.webcontainer.jsf.config] - Initializing Mojarra 2.2.13.SP1 20160303-1204 for context '/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT' <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:21,121 ThreadId:130 INFO  [logger: javax.enterprise.resource.webcontainer.jsf.config] - Monitoring file:/C:/dev/appserver/wildfly/wildfly-10.1.0.Final/user_projects/domains/techlabTrunkPostgres/deployments/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war/WEB-INF/faces-config.xml for modifications <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:21,151 ThreadId:130 INFO  [logger: org.primefaces.webapp.PostConstructApplicationEventListener] - Running on PrimeFaces 6.0 <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:21,151 ThreadId:130 INFO  [logger: org.primefaces.extensions.application.PostConstructApplicationEventListener] - Running on PrimeFaces Extensions 6.0.0 <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:21,167 ThreadId:130 INFO  [logger: org.wildfly.extension.undertow] - WFLYUT0021: Registered web context: /wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT <LogContext:none> <ServerService Thread Pool -- 65>
####2017-11-01 18:20:21,187 ThreadId:64 INFO  [logger: org.jboss.as.server] - WFLYSRV0010: Deployed "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war" (runtime-name : "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war") <LogContext:none> <ServerService Thread Pool -- 37>

------------------------------------------------ START 2: (wildfly declares that the deployment is finished, and we discover it)
####2017-11-01 18:20:21,323 ThreadId:130 INFO  [logger: jmx.WildflyAppDeploymentMbeanListener] - 


 IMPORTANT NOTIFICATION: javax.management.Notification[source=jboss.as:deployment=wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war][type=deployment-deployed][message=WFLYSRV0234: Deployed "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war" (runtime-name : "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war")] . WE KNOW THAT THE APPLICATION wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war IS DEPLOYED. 

 But it was necessary to hard code the managed bean name 

 We do not want to be hard coding WAR names to do our registration.

 Ideally we want to query and find the app that is suitable.  <LogContext:none> <ServerService Thread Pool -- 65>
------------------------------------------------ FINISH 2: (wildfly declares that the deployment is finished, and we discover it)

 ####2017-11-01 18:20:21,485 ThreadId:24 INFO  [logger: org.jboss.as] - WFLYSRV0060: Http management interface listening on http://0.0.0.0:9990/management <LogContext:none> <Controller Boot Thread>


```


## PROBLEM 2:
Our listener might be a dirty component, since we are trying to register a listener that is an
applicaton scoped bean, since it is so convenient to have CDI on our source code classes.
Perhaps the JMX listener have to be really basic POJOS, but CDI beans seem to work just fine
on the first time we register.
If we however remove the WAR file and add the WAR a second time from eclipse
we get the following exception wht the APP server sends the notification:

```

####2017-11-01 18:19:22,544 ThreadId:58 INFO  [logger: org.jboss.as.server] - WFLYSRV0010: Deployed "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war" (runtime-name : "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war") <LogContext:none> <DeploymentScanner-threads - 1>
####2017-11-01 18:19:22,546 ThreadId:43 WARN  [logger: org.jboss.as.controller] - WFLYCTL0356: Failed to emit notification Notification{type='deployment-deployed', source=[("deployment" => "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war")], message='WFLYSRV0234: Deployed "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war" (runtime-name : "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war")', timestamp=1509556761396, data={
    "name" => "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war",
    "server-booting" => false,
    "owner" => [
        ("subsystem" => "deployment-scanner"),
        ("scanner" => "default")
    ],
    "deployment" => "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war"
}}: org.jboss.weld.context.ContextNotActiveException: WELD-001303: No active contexts for scope type javax.enterprise.context.ApplicationScoped
	at org.jboss.weld.manager.BeanManagerImpl.getContext(BeanManagerImpl.java:689)
	at org.jboss.weld.bean.ContextualInstanceStrategy$DefaultContextualInstanceStrategy.getIfExists(ContextualInstanceStrategy.java:90)
	at org.jboss.weld.bean.ContextualInstanceStrategy$ApplicationScopedContextualInstanceStrategy.getIfExists(ContextualInstanceStrategy.java:124)
	at org.jboss.weld.bean.ContextualInstance.getIfExists(ContextualInstance.java:63)
	at org.jboss.weld.bean.proxy.ContextBeanInstance.getInstance(ContextBeanInstance.java:83)
	at org.jboss.weld.bean.proxy.ProxyMethodHandler.getInstance(ProxyMethodHandler.java:125)
	at jmx.WildflyAppDeploymentMbeanListener$Proxy$_$$_WeldClientProxy.isNotificationEnabled(Unknown Source)
	at org.jboss.as.jmx.model.ModelControllerMBeanServerPlugin$JMXNotificationHandler.isNotificationEnabled(ModelControllerMBeanServerPlugin.java:310)
	at org.jboss.as.controller.registry.NotificationHandlerNodeRegistry.findEntries(NotificationHandlerNodeRegistry.java:108)
	at org.jboss.as.controller.registry.NotificationHandlerNodeSubregistry.findHandlers(NotificationHandlerNodeSubregistry.java:97)
	at org.jboss.as.controller.registry.NotificationHandlerNodeRegistry.findEntries(NotificationHandlerNodeRegistry.java:121)
	at org.jboss.as.controller.registry.ConcreteNotificationHandlerRegistration.findMatchingNotificationHandlers(ConcreteNotificationHandlerRegistration.java:84)
	at org.jboss.as.controller.notification.NotificationSupports.fireNotifications(NotificationSupports.java:125)
	at org.jboss.as.controller.notification.NotificationSupports.access$000(NotificationSupports.java:47)
	at org.jboss.as.controller.notification.NotificationSupports$NonBlockingNotificationSupport$1.run(NotificationSupports.java:105)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
	at java.lang.Thread.run(Unknown Source)
	at org.jboss.threads.JBossThread.run(JBossThread.java:320)
 <LogContext:none> <ServerService Thread Pool -- 18>
####2017-11-01 18:19:57,636 ThreadId:303 INFO  [logger: org.wildfly.extension.undertow] - WFLYUT0022: Unregistered web context: /wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT <LogContext:none> <ServerService Thread Pool -- 113>
####2017-11-01 18:19:57,794 ThreadId:21 INFO  [logger: org.jboss.as.server.deployment] - WFLYSRV0028: Stopped deployment wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war (runtime-name: wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war) in 161ms <LogContext:none> <MSC service thread 1-7>



```

## PROBLEM 3: No deployed notification if in eclipse we click "restart" on the WAR application

In this scenario we have our application deployed for the first time.
For whatever reason, we decide we want to restart our WAR.
In this scenario, our application will not get any notification that the war is either "deployed" or restarted.


```
####2017-11-01 18:32:37,754 ThreadId:19 INFO  [logger: org.jboss.weld.deployer] - WFLYWELD0003: Processing weld deployment wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war <LogContext:none> <MSC service thread 1-5>
####2017-11-01 18:32:37,762 ThreadId:19 INFO  [logger: org.jboss.as.ejb3.deployment] - WFLYEJB0473: JNDI bindings for session bean named 'RegisterAppDeploymentStartupEjb' in deployment unit 'deployment "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war"' are as follows:

	java:global/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb!jmx.RegisterAppDeploymentStartupEjb
	java:app/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb!jmx.RegisterAppDeploymentStartupEjb
	java:module/RegisterAppDeploymentStartupEjb!jmx.RegisterAppDeploymentStartupEjb
	java:global/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb
	java:app/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT/RegisterAppDeploymentStartupEjb
	java:module/RegisterAppDeploymentStartupEjb
 <LogContext:none> <MSC service thread 1-5>
####2017-11-01 18:32:38,026 ThreadId:304 INFO  [logger: jmx.RegisterAppDeploymentStartupEjb] - 



Staring the registration process of a jmx notification listener <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,033 ThreadId:304 INFO  [logger: jmx.RegisterAppDeploymentStartupEjb] - We have found application deployments: [jboss.as:deployment=wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war]. We are returning the first deployment.  <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,033 ThreadId:304 INFO  [logger: jmx.RegisterAppDeploymentStartupEjb] - Registration process finished using brute force hard coded approach. We should seen after the application deployed message from wildfly our own messge coming from the listener. 



 <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,059 ThreadId:304 INFO  [logger: javax.enterprise.resource.webcontainer.jsf.config] - Initializing Mojarra 2.2.13.SP1 20160303-1204 for context '/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT' <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,485 ThreadId:304 INFO  [logger: javax.enterprise.resource.webcontainer.jsf.config] - Monitoring file:/C:/dev/appserver/wildfly/wildfly-10.1.0.Final/user_projects/domains/techlabTrunkPostgres/deployments/wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war/WEB-INF/faces-config.xml for modifications <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,487 ThreadId:304 INFO  [logger: org.primefaces.webapp.PostConstructApplicationEventListener] - Running on PrimeFaces 6.0 <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,487 ThreadId:304 INFO  [logger: org.primefaces.extensions.application.PostConstructApplicationEventListener] - Running on PrimeFaces Extensions 6.0.0 <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,488 ThreadId:304 INFO  [logger: org.wildfly.extension.undertow] - WFLYUT0021: Registered web context: /wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT <LogContext:none> <ServerService Thread Pool -- 119>
####2017-11-01 18:32:38,537 ThreadId:59 INFO  [logger: org.jboss.as.server] - WFLYSRV0016: Replaced deployment "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war" with deployment "wildfly-jmx-query-appdeploymentbean-1.0.0-SANPSHOT.war" <LogContext:none> <DeploymentScanner-threads - 2>

```


## ISSUE OPEN ON:
https://issues.jboss.org/browse/WFCORE-3387
