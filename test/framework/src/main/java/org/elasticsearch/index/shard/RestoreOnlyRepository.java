/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.index.shard;

import org.apache.lucene.index.IndexCommit;
import org.elasticsearch.Version;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.snapshots.IndexShardSnapshotStatus;
import org.elasticsearch.index.store.Store;
import org.elasticsearch.repositories.IndexId;
import org.elasticsearch.repositories.Repository;
import org.elasticsearch.repositories.RepositoryData;
import org.elasticsearch.snapshots.SnapshotId;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.elasticsearch.snapshots.SnapshotShardFailure;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.elasticsearch.repositories.RepositoryData.EMPTY_REPO_GEN;

/** A dummy repository for testing which just needs restore overridden */
public abstract class RestoreOnlyRepository extends AbstractLifecycleComponent implements Repository {
    private final String indexName;

    public RestoreOnlyRepository(String indexName) {
        this.indexName = indexName;
    }

    @Override
    protected void doStart() {
    }

    @Override
    protected void doStop() {
    }

    @Override
    protected void doClose() {
    }

    @Override
    public RepositoryMetaData getMetadata() {
        return null;
    }

    @Override
    public SnapshotInfo getSnapshotInfo(SnapshotId snapshotId) {
        return null;
    }

    @Override
    public MetaData getSnapshotGlobalMetaData(SnapshotId snapshotId) {
        return null;
    }

    @Override
    public IndexMetaData getSnapshotIndexMetaData(SnapshotId snapshotId, IndexId index) throws IOException {
        return null;
    }

    @Override
    public RepositoryData getRepositoryData() {
        Map<IndexId, Set<SnapshotId>> map = new HashMap<>();
        map.put(new IndexId(indexName, "blah"), emptySet());
        return new RepositoryData(EMPTY_REPO_GEN, Collections.emptyMap(), Collections.emptyMap(), map);
    }

    @Override
    public void finalizeSnapshot(SnapshotId snapshotId, List<IndexId> indices, long startTime, String failure,
                                 int totalShards, List<SnapshotShardFailure> shardFailures, long repositoryStateId,
                                 boolean includeGlobalState, MetaData metaData, Map<String, Object> userMetadata,
                                 ActionListener<SnapshotInfo> listener) {
        listener.onResponse(null);
    }

    @Override
    public void deleteSnapshot(SnapshotId snapshotId, long repositoryStateId, ActionListener<Void> listener) {
        listener.onResponse(null);
    }

    @Override
    public long getSnapshotThrottleTimeInNanos() {
        return 0;
    }

    @Override
    public long getRestoreThrottleTimeInNanos() {
        return 0;
    }

    @Override
    public String startVerification() {
        return null;
    }

    @Override
    public void endVerification(String verificationToken) {
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void snapshotShard(Store store, MapperService mapperService, SnapshotId snapshotId, IndexId indexId,
                              IndexCommit snapshotIndexCommit, IndexShardSnapshotStatus snapshotStatus, ActionListener<String> listener) {
    }

    @Override
    public IndexShardSnapshotStatus getShardSnapshotStatus(SnapshotId snapshotId, Version version, IndexId indexId, ShardId shardId) {
        return null;
    }

    @Override
    public void verify(String verificationToken, DiscoveryNode localNode) {
    }
}
