package com.github.gkislin.common.util;

import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.config.RootConfig;

import javax.management.*;
import java.lang.management.ManagementFactory;


public class JmxUtil {

    private static final LoggerWrapper LOGGER = LoggerWrapper.get(JmxUtil.class);
    private static final MBeanServer BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
    private static final String PREFIX = RootConfig.getProjectInfo().appName + ':';

    private JmxUtil() {
    }

    public static ObjectName registerMBean(Object mbean, String name) throws MalformedObjectNameException, MBeanRegistrationException, InstanceNotFoundException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        String mBeanName = PREFIX + name;
        LOGGER.info("Register MBean '" + mBeanName + "'");
        ObjectName oName = new ObjectName(mBeanName);
        if (BEAN_SERVER.isRegistered(oName)) {
            LOGGER.warn("MBean " + mBeanName + " already present. Re-register");
            BEAN_SERVER.unregisterMBean(oName);
        }
        BEAN_SERVER.registerMBean(mbean, oName);
        return oName;
    }

    public static void unregisterMBean(ObjectName oName) {
        if (oName == null) return;

        LOGGER.info("Unregister MBean " + oName);
        try {
            BEAN_SERVER.unregisterMBean(oName);
        } catch (Exception e) {
            LOGGER.error("MBean " + oName + " couldn't be unregistered", e);
        }
    }
}
