package com.sandy.courier.graph.linuxtest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;
import com.sandy.courier.common.util.FileUtil;
import com.sandy.courier.common.util.KgDgraphClient;
import com.sandy.courier.graph.biz.IGraphDataBiz;

/**
 * @Description: @createTime：2020/5/25 13:57
 * @author: chengyu3
 **/
@Component
public class GraphDataBizTest implements ApplicationRunner {

    @Autowired
    private IGraphDataBiz graphDataBiz;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (System.getProperty(KgDgraphClient.DGRAPH_ALPHA_HOST) != null) {
            run();
        }
    }

    private void run() throws Exception {
        String file = "triple.txt";
        String filePath = System.getProperty("user.dir");
        Long begin = System.currentTimeMillis();
        System.out.println("user.dir:" + filePath + File.separator + file);
        Integer aLong = FileUtil.readLines(filePath + File.separator + file,
                new DgraphMutationTextLineProcessor(200000L, 500));
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
            if (count.get() % 500 == 0) {
                System.out.println("count:" + count);
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

        private boolean process(String triple) {
            // graphDataBiz.saveWithRdf(triple);
            // long begin = System.currentTimeMillis();
            graphDataBiz.saveTripleWithTab(triple, "Category", "relation");
            // System.out.println("time spent:" + (System.currentTimeMillis() -
            // begin));
            return true;
        }

        private boolean processBatch(String triple) {
            if (batchSize > triples.size()) {
                triples.add(triple);
                return true;
            }
            long begin = System.currentTimeMillis();
            graphDataBiz.batchSaveTripleWithTab(triples, "Category", "relation");
            System.out.println("time cost:" + (System.currentTimeMillis() - begin));
            triples.clear();
            return true;
        }

        @Override
        public Integer getResult() {
            return count.get();
        }

    }
}
