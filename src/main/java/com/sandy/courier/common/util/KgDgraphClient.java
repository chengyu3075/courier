package com.sandy.courier.common.util;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.grpc.*;

/**
 * @Description: @createTime：2020/5/18 17:24
 * @author: chengyu3
 **/
public class KgDgraphClient {
    public static final String DGRAPH_ALPHA_HOST = "dgraph.alpha.host";
    private static DgraphClient dgraphClient;
    private static List<HostAndPort> hostAndPortLists = Lists.newArrayList(HostAndPort.fromParts("10.110.13.28", 9080),
            HostAndPort.fromParts("10.110.13.56", 9080), HostAndPort.fromParts("10.110.14.239", 9080));

    static {
        String alphaHost = System.getProperty(DGRAPH_ALPHA_HOST);
        System.out.println("alphaHost" + ":" + alphaHost);
        if (Strings.isNotBlank(alphaHost)) {
            dgraphClient = getClient(alphaHost.split(","));
        } else {
            dgraphClient = getDgraphClient(hostAndPortLists);
        }
    }

    private static DgraphClient getDgraphClient(List<HostAndPort> hostAndPorts) {
        if (hostAndPorts.size() == 0) {
            throw new Error("DgraphClient init error, no host available");
        }
        List<ManagedChannel> channels = hostAndPorts.stream()
                .map(e -> ManagedChannelBuilder.forAddress(e.getHost(), e.getPort()).usePlaintext().build())
                .collect(Collectors.toList());
        List<DgraphGrpc.DgraphStub> dgraphStubs = channels.stream().map(channel -> {
            DgraphGrpc.DgraphStub dgraphStub = DgraphGrpc.newStub(channel);
            ClientInterceptor timeoutInterceptor = new ClientInterceptor() {
                @Override
                public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                        CallOptions callOptions, Channel next) {
                    return next.newCall(method, callOptions.withDeadlineAfter(500, TimeUnit.MILLISECONDS));
                }
            };
            dgraphStub.withInterceptors(timeoutInterceptor);
            return dgraphStub;
        }).collect(Collectors.toList());
        return new DgraphClient(dgraphStubs.toArray(new DgraphGrpc.DgraphStub[hostAndPorts.size()]));
    }

    private static DgraphClient getClient(String... hosts) {
        List<HostAndPort> hostAndPorts = Lists.newArrayListWithCapacity(hosts.length);
        for (String host : hosts) {
            String[] split = host.split(":");
            hostAndPorts.add(HostAndPort.fromParts(split[0], Integer.parseInt(split[1])));
        }
        return getDgraphClient(hostAndPorts);
    }

    /**
     * 创建Dgraph客户端
     * 
     * @param
     * @return io.dgraph.DgraphClient @createTime：2020/5/18 17:46
     * @author: chengyu3
     */
    public static DgraphClient getClient() {
        return dgraphClient;
    }

    /**
     * 重置客户端
     * 
     * @param e
     * @return void @createTime：2020/5/31 11:08
     * @author: chengyu3
     */
    public static void resetClientIfNessasery(Throwable e) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (hasErrorHost(cause.getMessage())) {
                dgraphClient = getDgraphClient(hostAndPortLists);
                return;
            }
            cause = cause.getCause();
        }
        throw new RuntimeException(e);
    }

    private static boolean hasErrorHost(String errorMsg) {
        if (StringUtils.isBlank(errorMsg)) {
            return false;
        }
        Iterator<HostAndPort> iterator = hostAndPortLists.iterator();
        while (iterator.hasNext()) {
            HostAndPort next = iterator.next();
            String hostAndPortString = next.getHost() + ":" + next.getPort();
            if (errorMsg.contains(hostAndPortString)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public static Integer availableAlphaNodeCount() {
        return hostAndPortLists.size();
    }
}
