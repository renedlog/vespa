// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.documentapi.messagebus.loadtypes.test;

import com.yahoo.documentapi.messagebus.loadtypes.LoadTypeSet;
import junit.framework.TestCase;

/**
 * @author thomasg
 */
public class LoadTypesTestCase extends TestCase {
    public void testIdGeneration() {
        LoadTypeSet set = new LoadTypeSet();
        set.addType("vespagrim", "VERY_HIGH");
        set.addType("slow", "VERY_LOW");
        set.addType("test", null);

        assertEquals("vespagrim", set.getNameMap().get("vespagrim").getName());
        assertEquals("slow", set.getNameMap().get("slow").getName());
        assertEquals("test", set.getNameMap().get("test").getName());
        assertEquals("default", set.getNameMap().get("default").getName());

        assertEquals(0xc21803d4, set.getNameMap().get("vespagrim").getId());
    }
}
