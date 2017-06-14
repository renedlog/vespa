// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.jdisc.http.filter;

import com.yahoo.jdisc.http.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class JDiscCookieWrapperTest {

    @Test
    public void requireThatWrapWorks() {
        Cookie cookie = new Cookie("name", "value");
        JDiscCookieWrapper wrapper = JDiscCookieWrapper.wrap(cookie);

        wrapper.setComment("comment");
        wrapper.setDomain("yahoo.com");
        wrapper.setMaxAge(10);
        wrapper.setPath("/path");
        wrapper.setVersion(1);

        Assert.assertEquals(wrapper.getName(), cookie.getName());
        Assert.assertEquals(wrapper.getValue(), cookie.getValue());
        Assert.assertEquals(wrapper.getDomain(), cookie.getDomain());
        Assert.assertEquals(wrapper.getComment(), cookie.getComment());
        Assert.assertEquals(wrapper.getMaxAge(), cookie.getMaxAge(TimeUnit.SECONDS));
        Assert.assertEquals(wrapper.getPath(), cookie.getPath());
        Assert.assertEquals(wrapper.getVersion(), cookie.getVersion());
        Assert.assertEquals(wrapper.getSecure(), cookie.isSecure());

    }
}
