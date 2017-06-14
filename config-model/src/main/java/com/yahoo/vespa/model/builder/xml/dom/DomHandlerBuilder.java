// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.model.builder.xml.dom;

import com.yahoo.container.bundle.BundleInstantiationSpecification;
import com.yahoo.osgi.provider.model.ComponentModel;
import com.yahoo.text.XML;
import com.yahoo.config.model.producer.AbstractConfigProducer;
import com.yahoo.vespa.model.container.component.Component;
import com.yahoo.vespa.model.container.component.Handler;
import com.yahoo.vespa.model.container.xml.BundleInstantiationSpecificationBuilder;
import org.w3c.dom.Element;

/**
 * @author gjoranv
 * @since 5.1.6
 */
public class DomHandlerBuilder extends VespaDomBuilder.DomConfigProducerBuilder<Handler> {
    private final boolean legacyMode;

    public DomHandlerBuilder(boolean legacyMode) {
        this.legacyMode = legacyMode;
    }

    public DomHandlerBuilder() {
        this(false);
    }

    @Override
    protected Handler doBuild(AbstractConfigProducer ancestor, Element handlerElement) {
        Handler<? super Component<?, ?>> handler = getHandler(handlerElement);

        for (Element binding : XML.getChildren(handlerElement, "binding"))
            handler.addServerBindings(XML.getValue(binding));

        for (Element clientBinding : XML.getChildren(handlerElement, "clientBinding"))
            handler.addClientBindings(XML.getValue(clientBinding));

        DomComponentBuilder.addChildren(ancestor, handlerElement, handler);

        return handler;
    }

    protected Handler<? super Component<?, ?>> getHandler(Element handlerElement) {
        BundleInstantiationSpecification bundleSpec = BundleInstantiationSpecificationBuilder.build(handlerElement, legacyMode);
        return new Handler<>(
                new ComponentModel(bundleSpec));
    }
}
