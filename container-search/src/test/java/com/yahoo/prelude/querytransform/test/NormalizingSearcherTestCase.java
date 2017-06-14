// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.prelude.querytransform.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.yahoo.language.Linguistics;
import com.yahoo.language.simple.SimpleLinguistics;
import com.yahoo.prelude.Index;
import com.yahoo.prelude.SearchDefinition;
import com.yahoo.prelude.query.PhraseSegmentItem;
import com.yahoo.prelude.query.Substring;
import com.yahoo.prelude.query.WordAlternativesItem;
import com.yahoo.prelude.query.WordItem;
import com.yahoo.search.Query;
import com.yahoo.prelude.IndexFacts;
import com.yahoo.prelude.IndexModel;
import com.yahoo.prelude.querytransform.NormalizingSearcher;
import com.yahoo.search.searchchain.Execution;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bratseth
 */
public class NormalizingSearcherTestCase {

    private static final Linguistics linguistics = new SimpleLinguistics();

    @Test
    public void testNoNormalizingNecssary() {
        Query query = new Query("/search?query=bilen&search=cluster1&restrict=type1");
        createExecution().search(query);
        assertEquals("bilen", query.getModel().getQueryTree().getRoot().toString());
    }

    @Test
    public void testAttributeQuery() {
        Query query = new Query("/search?query=attribute:" + enc("b\u00e9yonc\u00e8 b\u00e9yonc\u00e8") + "&search=cluster1&restrict=type1");
        createExecution().search(query);
        assertEquals("AND attribute:b\u00e9yonc\u00e8 beyonce", query.getModel().getQueryTree().getRoot().toString());
    }

    @Test
    public void testOneTermNormalizing() {
        Query query = new Query("/search?query=b\u00e9yonc\u00e8&search=cluster1&restrict=type1");
        createExecution().search(query);
        assertEquals("beyonce", query.getModel().getQueryTree().getRoot().toString());
    }

    @Test
    public void testOneTermNoNormalizingDifferentSearchDef() {
        Query query = new Query("/search?query=b\u00e9yonc\u00e8&search=cluster1&restrict=type2");
        createExecution().search(query);
        assertEquals("béyoncè", query.getModel().getQueryTree().getRoot().toString());
    }

    @Test
    public void testTwoTermQuery() throws UnsupportedEncodingException {
        Query query = new Query("/search?query=" + enc("b\u00e9yonc\u00e8 beyonc\u00e9") + "&search=cluster1&restrict=type1");
        createExecution().search(query);
        assertEquals("AND beyonce beyonce", query.getModel().getQueryTree().getRoot().toString());
    }

    private String enc(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPhraseQuery() {
        Query query = new Query("/search?query=" + enc("\"b\u00e9yonc\u00e8 beyonc\u00e9\"") + "&search=cluster1&restrict=type1");
        query.setTraceLevel(2);
        createExecution().search(query);
        assertEquals("\"beyonce beyonce\"", query.getModel().getQueryTree().getRoot().toString());
    }

    @Test
    public void testLiteralBoost() {
        Query query = new Query("/search?query=nop&search=cluster1&restrict=type1");
        List<WordAlternativesItem.Alternative> terms = new ArrayList<>();
        Substring origin = new Substring(0, 5, "h\u00F4tels");
        terms.add(new WordAlternativesItem.Alternative("h\u00F4tels", 1.0d));
        terms.add(new WordAlternativesItem.Alternative("h\u00F4tel", 0.7d));
        query.getModel().getQueryTree().setRoot(new WordAlternativesItem("default", true, origin, terms));
        createExecution().search(query);
        WordAlternativesItem w = (WordAlternativesItem) query.getModel().getQueryTree().getRoot();
        assertEquals(4, w.getAlternatives().size());
        boolean foundHotel = false;
        for (WordAlternativesItem.Alternative a : w.getAlternatives()) {
            if ("hotel".equals(a.word)) {
                foundHotel = true;
                assertEquals(.7d * .7d, a.exactness, 1e-15);
            }
        }
        assertTrue("Did not find the expected normalized form \"hotel\".", foundHotel);
    }


    @Test
    public void testPhraseSegmentNormalization() {
        Query query = new Query("/search?query=&search=cluster1&restrict=type1");
        PhraseSegmentItem phraseSegment = new PhraseSegmentItem("default", false, false);
        phraseSegment.addItem(new WordItem("net"));
        query.getModel().getQueryTree().setRoot(phraseSegment);
        assertEquals("'net'", query.getModel().getQueryTree().getRoot().toString());
        createExecution().search(query);
        assertEquals("'net'", query.getModel().getQueryTree().getRoot().toString());
    }

    private Execution createExecution() {
        return new Execution(new NormalizingSearcher(linguistics),
                             Execution.Context.createContextStub(null, createIndexFacts(), linguistics));
    }

    private IndexFacts createIndexFacts() {
        Map<String, List<String>> clusters = new LinkedHashMap<>();
        clusters.put("cluster1", Arrays.asList("type1", "type2", "type3"));
        clusters.put("cluster2", Arrays.asList("type4", "type5"));
        Map<String, SearchDefinition> searchDefs = new LinkedHashMap<>();
        searchDefs.put("type1", createSearchDefinitionWithFields("type1", true));
        searchDefs.put("type2", createSearchDefinitionWithFields("type2", false));
        searchDefs.put("type3", new SearchDefinition("type3"));
        searchDefs.put("type3", new SearchDefinition("type3"));
        searchDefs.put("type4", new SearchDefinition("type4"));
        searchDefs.put("type5", new SearchDefinition("type5"));
        SearchDefinition union = new SearchDefinition("union");
        return new IndexFacts(new IndexModel(clusters, searchDefs, union));
    }

    private SearchDefinition createSearchDefinitionWithFields(String name, boolean normalize) {
        SearchDefinition type = new SearchDefinition(name);

        Index defaultIndex = new Index("default");
        defaultIndex.setNormalize(normalize);
        type.addIndex(defaultIndex);

        Index absoluteIndex = new Index("absolute");
        absoluteIndex.setNormalize(normalize);
        type.addIndex(absoluteIndex);

        Index normalizercheckIndex = new Index("normalizercheck");
        normalizercheckIndex.setNormalize(normalize);
        type.addIndex(normalizercheckIndex);

        Index attributeIndex = new Index("attribute");
        attributeIndex.setAttribute(true);
        type.addIndex(attributeIndex);

        return type;
    }

}
