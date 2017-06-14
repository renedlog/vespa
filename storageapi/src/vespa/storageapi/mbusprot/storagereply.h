// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
#pragma once

#include "storagemessage.h"
#include "storageprotocol.h"
#include <vespa/messagebus/reply.h>
#include <vespa/storageapi/messageapi/storagereply.h>

namespace storage {
namespace mbusprot {

class StorageReply : public mbus::Reply, public StorageMessage {
    const ProtocolSerialization* _serializer;
    mutable vespalib::alloc::Alloc _buffer;
    uint32_t _mbusType;
    mutable api::StorageReply::SP _reply;

public:
    typedef std::unique_ptr<StorageReply> UP;

    StorageReply(const mbus::BlobRef& data, const ProtocolSerialization&);
    StorageReply(const api::StorageReply::SP& reply);
    ~StorageReply();

    const mbus::string& getProtocol() const override { return StorageProtocol::NAME; }

    uint32_t getType() const override { return _mbusType; }

    const api::StorageReply::SP& getReply() { deserialize(); return _reply; }
    api::StorageReply::CSP getReply() const { deserialize(); return _reply; }

    api::StorageMessage::SP getInternalMessage() override { deserialize(); return _reply; }
    api::StorageMessage::CSP getInternalMessage() const override { deserialize(); return _reply; }

    uint8_t priority() const override {
        if (_reply.get()) {
            return _reply->getPriority();
        }
        return 0;
    }

private:

    void deserialize() const;
};

} // mbusprot
} // storage
