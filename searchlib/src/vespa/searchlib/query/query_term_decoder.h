// Copyright 2017 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.

#pragma once

#include "queryterm.h"
#include <vespa/vespalib/stllike/string.h>

namespace search {

/**
 * Class used to decode a single term.
 */ 
struct QueryTermDecoder {
    using QueryPacketT = vespalib::stringref;

    static QueryTermSimple::UP decodeTerm(QueryPacketT term);
};

}
