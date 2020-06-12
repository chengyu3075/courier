package com.sandy.courier.graph.biz.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sandy.courier.common.exception.GraphDbException;
import com.sandy.courier.common.util.StringUtil;
import com.sandy.courier.graph.biz.AbstractDgraphBiz;
import com.sandy.courier.graph.biz.IGraphDataBiz;

/**
 * @Description: @createTime：2020/5/20 11:49
 * @author: chengyu3
 **/
@Service
public class DgraphBizImpl extends AbstractDgraphBiz implements IGraphDataBiz {

    final String DGRAPH_TYPE = "dgraph.type";

    final String PROPERTY_NAME = "name";

    final String RELATION_NAME = "rel";

    final String tab = "\t";

    @Override
    public void saveWithRdf(String rdf) throws GraphDbException {
        mutationWithTransaction(rdf);
    }

    private String convertRdf(String rdf) {
        return rdf;
    }

    @Override
    public void batchSaveWithRdf(List<String> rdfs) throws GraphDbException {
        Preconditions.checkArgument(rdfs != null && rdfs.size() > 0);
        String join = Strings.join(rdfs.iterator(), '\n');
        saveWithRdf(join);
    }

    @Override
    public void saveTripleWithTab(String triple, String typeName, String relationName) throws GraphDbException {
        Preconditions.checkArgument(Strings.isNotBlank(triple));
        Preconditions.checkArgument(Strings.isNotBlank(typeName));
        batchSaveTripleWithTab(Lists.newArrayList(triple), typeName, relationName);
    }

    @Override
    public void batchSaveTripleWithTab(List<String> triples, String typeName, String relationName)
            throws GraphDbException {
        Preconditions.checkNotNull(triples);
        Preconditions.checkArgument(StringUtils.isNotBlank(typeName));
        Preconditions.checkArgument(StringUtils.isNotBlank(relationName));
        // create query
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("{\n");
        Map<String, String> aliasMap = Maps.newHashMap();
        String property_name = getPROPERTY_NAME();
        for (int i = 0; i < triples.size(); i++) {
            String[] split = triples.get(i).split(tab);
            if (split.length != 3) {
                LoggerFactory.getLogger(this.getClass()).error("error triple:" + triples.get(i));
                continue;
            }
            String srcNodeName = StringUtil.escape(split[0]);
            String dstNodeName = StringUtil.escape(split[2]);

            if (!aliasMap.containsKey(srcNodeName)) {
                queryBuffer.append("src" + i + "(func: eq(" + property_name + ", \"" + srcNodeName
                        + "\")) @filter(type(" + typeName + ")){\n" + "        src" + i + " as uid\n" + " }\n");
                aliasMap.put(srcNodeName, "src" + i);
            }
            if (!aliasMap.containsKey(dstNodeName)) {
                queryBuffer.append("dst" + i + "(func: eq(" + property_name + ", \"" + dstNodeName
                        + "\")) @filter(type(" + typeName + ")) {\n" + "        dst" + i + " as uid\n" + "    }\n");
                aliasMap.put(dstNodeName, "dst" + i);
            }
        }
        queryBuffer.append("}\n");
        try {
            // create mutation
            String mutation = createUpsertMutationJson(triples, typeName, relationName, aliasMap, property_name);
            upsertWithJson(mutation, queryBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建突变json
     * 
     * @param triples
     * @param typeName
     * @param relationName
     * @return java.lang.String @createTime：2020/5/22 10:45
     * @author: chengyu3
     */
    private String createUpsertMutationJson(List<String> triples, String typeName, String relationName,
            Map<String, String> aliasMap, String propName) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (int i = 0; i < triples.size(); i++) {
            String triple = triples.get(i);
            String[] split = triple.split(tab);
            String srcNodeName = StringUtil.escape(split[0]);
            String relation = StringUtil.escape(split[1]);
            String dstNodeName = StringUtil.escape(split[2]);
            ObjectNode srcNode = objectMapper.createObjectNode();
            ObjectNode dstNode = objectMapper.createObjectNode();
            ((ObjectNode) srcNode).put(DGRAPH_TYPE, typeName);
            ((ObjectNode) srcNode).put(propName, srcNodeName);
            ((ObjectNode) srcNode).put("uid", "uid(" + aliasMap.get(srcNodeName) + ")");

            ((ObjectNode) dstNode).put(DGRAPH_TYPE, typeName);
            ((ObjectNode) dstNode).put(propName, dstNodeName);
            ((ObjectNode) dstNode).put("uid", "uid(" + aliasMap.get(dstNodeName) + ")");
            ((ObjectNode) dstNode).put(RELATION_NAME + "|" + relationName, relation);
            srcNode.putPOJO(RELATION_NAME, dstNode);
            arrayNode.add(srcNode);
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
    }

    private String getPROPERTY_NAME() {
        return PROPERTY_NAME;
    }
}
