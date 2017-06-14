// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.

#include "indexbuilder.h"

namespace search::index {

IndexBuilder::IndexBuilder(const Schema &schema)
    : _schema(schema)
{ }

IndexBuilder::~IndexBuilder() { }

}
