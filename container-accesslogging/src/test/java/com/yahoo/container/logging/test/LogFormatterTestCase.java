// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.container.logging.test;

import java.util.Date;

import com.yahoo.container.logging.LogFormatter;

/**
 * @author <a href="mailto:travisb@yahoo-inc.com">Bob Travis</a>
 */
public class LogFormatterTestCase extends junit.framework.TestCase {

    public LogFormatterTestCase(String name) {
        super(name);
    }

    public void testIt() {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        @SuppressWarnings("deprecation")
        long time = new Date(103,7,25,13,30,35).getTime();
        String result = LogFormatter.insertDate("test%Y%m%d%H%M%S%x",time);
        assertEquals("test20030825133035Aug",result);
        result = LogFormatter.insertDate("test%s%T",time);
        assertEquals("test000"+time, result);
    }

}
