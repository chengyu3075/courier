package com.sandy.courier.graph;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;
import com.sandy.courier.CourierApplicationTest;
import com.sandy.courier.common.util.FileUtil;
import com.sandy.courier.common.util.KgDgraphClient;
import com.sandy.courier.graph.biz.IGraphDataBiz;

/**
 * @Description: @createTime：2020/5/20 14:57
 * @author: chengyu3
 **/
public class IGraphDataBizTest extends CourierApplicationTest {

    @Autowired
    private IGraphDataBiz graphDataBiz;

    @Test
    public void testSaveTripleWithTab() throws Exception {
        String filePath = "D:\\迅雷下载\\baike_triples.txt";
        // String rdfPath = "C:\\Users\\chengyu3\\Desktop\\g01.rdf";
        String filePath1 = "C:\\Users\\chengyu3\\Desktop\\test.txt";
        if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
            filePath = "/root/software/triples.txt";
        }
        System.setProperty(KgDgraphClient.DGRAPH_ALPHA_HOST, "10.110.13.28:9080,10.110.13.56:9080,10.110.14.239:9080");
        Long begin = System.currentTimeMillis();
        Integer aLong = FileUtil.readLines(filePath, new DgraphMutationTextLineProcessor(400L, 20));
        System.out.println("count:" + aLong);
        System.out.println("time spent:" + (System.currentTimeMillis() - begin) + "");
    }

    public class DgraphMutationTextLineProcessor implements LineProcessor<Integer> {
        private List<String> triples;
        private AtomicInteger count = new AtomicInteger(0);
        private Long maxCount;
        private Integer batchSize;

        public DgraphMutationTextLineProcessor(Long maxCount, Integer batchSize) {
            Preconditions.checkNotNull(maxCount);
            Preconditions.checkNotNull(batchSize);
            Preconditions.checkArgument(maxCount > 0L);
            this.maxCount = maxCount;
            this.batchSize = batchSize;
            triples = Lists.newArrayListWithCapacity(batchSize);
        }

        public DgraphMutationTextLineProcessor(Long maxCount) {
            Preconditions.checkNotNull(maxCount);
            this.maxCount = maxCount;
        }

        @Override
        public boolean processLine(String s) throws IOException {
            System.out.println("count:" + count + ",s:" + s);
            if (count.get() % 500 == 0) {
            }
            if (batchSize == null) {
                process(s);
            } else {
                processBatch(s);
            }
            count.incrementAndGet();
            if (maxCount != null) {
                if (count.longValue() == maxCount.longValue() || count.longValue() > maxCount.longValue()) {
                    return false;
                }
            }
            return true;
        }

        private boolean process(String triple) throws IOException {
            // FileUtil.writeToFile(new File("D:\\triple.txt"), triple + "\n",
            // true);
            // graphDataBiz.saveTripleWithTab(triple, "Category", "relation");
            return true;
        }

        private boolean processBatch(String triple) {
            triples.add(triple);
            if (batchSize > triples.size()) {
                return true;
            }
            graphDataBiz.batchSaveTripleWithTab(triples, "Category", "rel_name");
            triples.clear();
            return true;
        }

        @Override
        public Integer getResult() {
            return count.get();
        }

    }
}
