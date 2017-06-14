// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.searchlib.expression;

import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author <a href="mailto:simon@yahoo-inc.com">Simon Thoresen</a>
 */
public class FixedWidthBucketFunctionTestCase {

    @Test
    public void requireThatAccessorsWork() {
        ExpressionNode arg = new AttributeNode("foo");
        NumericResultNode width = new IntegerResultNode(69L);
        FixedWidthBucketFunctionNode node = new FixedWidthBucketFunctionNode(width, arg);
        assertSame(arg, node.getArg());
        assertSame(width, node.getWidth());
    }
}
