package com.sandy.courier.graph.bean;

import java.util.List;

/**
 * @Description:三元组 @createTime：2020/5/20 11:19
 * @author: chengyu3
 **/
public class GraphTripleBean {

    private GraphNodeBean srcNode;

    private GraphNodeBean dstNode;

    private List<GraphRelationBean> relations;

    public GraphNodeBean getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(GraphNodeBean srcNode) {
        this.srcNode = srcNode;
    }

    public GraphNodeBean getDstNode() {
        return dstNode;
    }

    public void setDstNode(GraphNodeBean dstNode) {
        this.dstNode = dstNode;
    }

    public List<GraphRelationBean> getRelations() {
        return relations;
    }

    public void setRelations(List<GraphRelationBean> relations) {
        this.relations = relations;
    }

}
