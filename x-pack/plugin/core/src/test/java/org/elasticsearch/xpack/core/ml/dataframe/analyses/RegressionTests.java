/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.core.ml.dataframe.analyses;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.test.AbstractSerializingTestCase;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class RegressionTests extends AbstractSerializingTestCase<Regression> {

    @Override
    protected Regression doParseInstance(XContentParser parser) throws IOException {
        return Regression.fromXContent(parser, false);
    }

    @Override
    protected Regression createTestInstance() {
        return createRandom();
    }

    public static Regression createRandom() {
        String dependentVariableName = randomAlphaOfLength(10);
        BoostedTreeParams boostedTreeParams = BoostedTreeParamsTests.createRandom();
        String predictionFieldName = randomBoolean() ? null : randomAlphaOfLength(10);
        Double trainingPercent = randomBoolean() ? null : randomDoubleBetween(1.0, 100.0, true);
        return new Regression(dependentVariableName, boostedTreeParams, predictionFieldName, trainingPercent);
    }

    @Override
    protected Writeable.Reader<Regression> instanceReader() {
        return Regression::new;
    }

    public void testConstructor_GivenTrainingPercentIsNull() {
        Regression regression = new Regression("foo", new BoostedTreeParams(0.0, 0.0, 0.5, 500, 1.0), "result", null);
        assertThat(regression.getTrainingPercent(), equalTo(100.0));
    }

    public void testConstructor_GivenTrainingPercentIsBoundary() {
        Regression regression = new Regression("foo", new BoostedTreeParams(0.0, 0.0, 0.5, 500, 1.0), "result", 1.0);
        assertThat(regression.getTrainingPercent(), equalTo(1.0));
        regression = new Regression("foo", new BoostedTreeParams(0.0, 0.0, 0.5, 500, 1.0), "result", 100.0);
        assertThat(regression.getTrainingPercent(), equalTo(100.0));
    }

    public void testConstructor_GivenTrainingPercentIsLessThanOne() {
        ElasticsearchStatusException e = expectThrows(ElasticsearchStatusException.class,
            () -> new Regression("foo", new BoostedTreeParams(0.0, 0.0, 0.5, 500, 1.0), "result", 0.999));

        assertThat(e.getMessage(), equalTo("[training_percent] must be a double in [1, 100]"));
    }

    public void testConstructor_GivenTrainingPercentIsGreaterThan100() {
        ElasticsearchStatusException e = expectThrows(ElasticsearchStatusException.class,
            () -> new Regression("foo", new BoostedTreeParams(0.0, 0.0, 0.5, 500, 1.0), "result", 100.0001));

        assertThat(e.getMessage(), equalTo("[training_percent] must be a double in [1, 100]"));
    }

    public void testGetStateDocId() {
        Regression regression = createRandom();
        assertThat(regression.persistsState(), is(true));
        String randomId = randomAlphaOfLength(10);
        assertThat(regression.getStateDocId(randomId), equalTo(randomId + "_regression_state#1"));
    }
}
