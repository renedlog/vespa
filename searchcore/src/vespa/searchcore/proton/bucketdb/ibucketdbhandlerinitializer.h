// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.

#pragma once

namespace proton
{

namespace bucketdb
{

/**
 * The IBucketDBHandlerInitiaizer class handles initialization of a 
 * BucketDBHandler.
 */
class IBucketDBHandlerInitializer
{
public:
    IBucketDBHandlerInitializer()
    {
    }

    virtual ~IBucketDBHandlerInitializer()
    {
    }

    virtual void
    addDocumentMetaStore(IDocumentMetaStore *dms,
                         search::SerialNum flushedSerialNum) = 0;
};

}

}
