// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.container.jdisc;

import com.yahoo.container.jdisc.metric.MetricConsumerProvider;
import com.yahoo.jdisc.application.ContainerThread;
import com.yahoo.jdisc.application.MetricConsumer;

import java.util.concurrent.ThreadFactory;

/**
 * @author <a href="mailto:simon@yahoo-inc.com">Simon Thoresen Hult</a>
 */
public class ContainerThreadFactory implements ThreadFactory {

    private final ThreadFactory delegate;

    public ContainerThreadFactory(MetricConsumerProvider metricConsumerProvider) {
        metricConsumerProvider.getClass(); // throws NullPointerException
        delegate = new ContainerThread.Factory(new com.google.inject.Provider<MetricConsumer>() {

            @Override
            public MetricConsumer get() {
                return metricConsumerProvider.newInstance();
            }
        });
    }

    @Override
    public Thread newThread(Runnable target) {
        return delegate.newThread(target);
    }

}
