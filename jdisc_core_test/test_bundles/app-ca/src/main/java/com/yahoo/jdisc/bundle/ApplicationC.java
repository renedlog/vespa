// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.jdisc.bundle;

import com.yahoo.jdisc.application.Application;
import com.yahoo.jdisc.bundle.a.CertificateA;

/**
 * @author <a href="mailto:simon@yahoo-inc.com">Simon Thoresen</a>
 */
public class ApplicationC implements Application {

    private final CertificateA certificateA = new CertificateA();

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }
}
