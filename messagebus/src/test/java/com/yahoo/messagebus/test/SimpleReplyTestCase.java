// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.messagebus.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:simon@yahoo-inc.com">Simon Thoresen Hult</a>
 */
public class SimpleReplyTestCase {

    @Test
    public void requireThatAccessorsWork() {
        SimpleReply reply = new SimpleReply("foo");
        assertEquals(SimpleProtocol.REPLY, reply.getType());
        assertEquals(SimpleProtocol.NAME, reply.getProtocol());
        assertEquals("foo", reply.getValue());
        reply.setValue("bar");
        assertEquals("bar", reply.getValue());
    }
}
