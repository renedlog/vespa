// Copyright 2018 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.flags.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.yahoo.vespa.flags.FetchVector;
import com.yahoo.vespa.flags.FlagId;
import com.yahoo.vespa.flags.FlagSource;
import com.yahoo.vespa.flags.RawFlag;
import com.yahoo.vespa.flags.json.wire.WireFlagData;
import com.yahoo.vespa.flags.json.wire.WireRule;

import javax.annotation.concurrent.Immutable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A data structure containing all data for a single flag, that can be serialized to/from JSON,
 * and that can be used to implement {@link FlagSource}.
 *
 * @author hakonhall
 */
@Immutable
public class FlagData {
    private final FlagId id;
    private final List<Rule> rules;
    private final FetchVector defaultFetchVector;

    public FlagData(FlagId id) {
        this(id, new FetchVector(), Collections.emptyList());
    }

    public FlagData(FlagId id, FetchVector defaultFetchVector, Rule... rules) {
        this(id, defaultFetchVector, Arrays.asList(rules));
    }

    public FlagData(FlagId id, FetchVector defaultFetchVector, List<Rule> rules) {
        this.id = id;
        this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
        this.defaultFetchVector = defaultFetchVector;
    }

    public FlagId id() {
        return id;
    }

    public Optional<RawFlag> resolve(FetchVector fetchVector) {
        return rules.stream()
                .filter(rule -> rule.match(defaultFetchVector.with(fetchVector)))
                .findFirst()
                .flatMap(Rule::getValueToApply);
    }

    public String serializeToJson() {
        return toWire().serializeToJson();
    }

    public byte[] serializeToUtf8Json() {
        return toWire().serializeToBytes();
    }

    public void serializeToOutputStream(OutputStream outputStream) {
        toWire().serializeToOutputStream(outputStream);
    }

    public JsonNode toJsonNode() {
        return toWire().serializeToJsonNode();
    }

    private WireFlagData toWire() {
        WireFlagData wireFlagData = new WireFlagData();

        wireFlagData.id = id.toString();

        if (!rules.isEmpty()) {
            wireFlagData.rules = rules.stream().map(Rule::toWire).collect(Collectors.toList());
        }

        wireFlagData.defaultFetchVector = FetchVectorHelper.toWire(defaultFetchVector);

        return wireFlagData;
    }

    public static FlagData deserializeUtf8Json(byte[] bytes) {
        return fromWire(WireFlagData.deserialize(bytes));
    }

    public static FlagData deserialize(InputStream inputStream) {
        return fromWire(WireFlagData.deserialize(inputStream));
    }

    public static FlagData deserialize(String string) {
        return fromWire(WireFlagData.deserialize(string));
    }

    private static FlagData fromWire(WireFlagData wireFlagData) {
        return new FlagData(
                new FlagId(Objects.requireNonNull(wireFlagData.id)),
                FetchVectorHelper.fromWire(wireFlagData.defaultFetchVector),
                rulesFromWire(wireFlagData.rules)
        );
    }

    private static List<Rule> rulesFromWire(List<WireRule> wireRules) {
        if (wireRules == null) return Collections.emptyList();
        return wireRules.stream().map(Rule::fromWire).collect(Collectors.toList());
    }
}

