package com.sandy.courier.graph.biz;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.protobuf.ByteString;
import com.sandy.courier.common.util.KgDgraphClient;

import io.dgraph.DgraphProto;

/**
 * @Description: @createTime：2020/5/20 11:51
 * @author: chengyu3
 **/
public abstract class AbstractDgraphBiz {

    protected final void mutationWithRdfJson(String rdfJson) {
        Preconditions.checkArgument(Strings.isNotBlank(rdfJson));
        DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(rdfJson))
                .build();
        DgraphProto.Request request = DgraphProto.Request.newBuilder().addMutations(mutation).setCommitNow(true)
                .build();
        mutationWithTransaction(request, KgDgraphClient.availableAlphaNodeCount());
    }

    protected final void upsertWithJson(String rdfJson, String query) {
        Preconditions.checkArgument(Strings.isNotBlank(rdfJson));
        Preconditions.checkArgument(Strings.isNotBlank(query));
        DgraphProto.Mutation mu1 = DgraphProto.Mutation.newBuilder()
                .setSetJson(ByteString.copyFromUtf8(rdfJson.toString())).build();
        DgraphProto.Request request = DgraphProto.Request.newBuilder().addMutations(mu1).setQuery(query)
                .setCommitNow(true).build();
        mutationWithTransaction(request, KgDgraphClient.availableAlphaNodeCount());
    }

    protected final void mutationWithTransaction(String rdfNquads) {
        Preconditions.checkArgument(rdfNquads != null && rdfNquads.trim().length() > 0);
        DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder()
                .setSetNquads(ByteString.copyFromUtf8(rdfNquads)).build();
        DgraphProto.Request request = DgraphProto.Request.newBuilder().addMutations(mutation).setCommitNow(true)
                .build();
        mutationWithTransaction(request, KgDgraphClient.availableAlphaNodeCount() - 1);
    }

    private void mutationWithTransaction(DgraphProto.Request request, Integer retryTimes) {
        try {
            DgraphProto.Response response = KgDgraphClient.getClient().newTransaction().doRequest(request);
            System.out.println(response);
        } catch (Exception e) {
            KgDgraphClient.resetClientIfNessasery(e);
            if (retryTimes > 0) {
                LoggerFactory.getLogger(this.getClass().getName()).error(e.getMessage());
                mutationWithTransaction(request, retryTimes--);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    protected final void setSchema(String schema) {
        Preconditions.checkArgument(schema != null && schema.trim().length() > 0);
        DgraphProto.Operation operation = DgraphProto.Operation.newBuilder().setSchema(schema).build();
        KgDgraphClient.getClient().alter(operation);
    }

    protected final void dropAll(boolean includeSchema) {
        if (includeSchema) {
            KgDgraphClient.getClient().alter(DgraphProto.Operation.newBuilder().setDropAll(true).build());
        } else {
            KgDgraphClient.getClient()
                    .alter(DgraphProto.Operation.newBuilder().setDropOp(DgraphProto.Operation.DropOp.DATA).build());
        }
    }

}
