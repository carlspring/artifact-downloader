package org.carlspring.rest.app;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyStorageApplication
        extends ResourceConfig
{

    private static final Logger logger = LoggerFactory.getLogger(DummyStorageApplication.class);


    public DummyStorageApplication()
    {
        if (logger.isDebugEnabled())
        {
            register(new LoggingFilter());
        }
    }

}
