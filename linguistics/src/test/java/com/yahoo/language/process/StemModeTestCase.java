// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.language.process;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:simon@yahoo-inc.com">Simon Thoresen Hult</a>
 */
public class StemModeTestCase {

    @Test
    @SuppressWarnings("deprecation")
    public void requireThatValueOfWorks() {
        for (StemMode mode : StemMode.values()) {
            assertEquals(mode, StemMode.valueOf(mode.getValue()));
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void requireThatValueOfUnknownIsNone() {
        assertEquals(StemMode.NONE, StemMode.valueOf(-1));
    }

}
