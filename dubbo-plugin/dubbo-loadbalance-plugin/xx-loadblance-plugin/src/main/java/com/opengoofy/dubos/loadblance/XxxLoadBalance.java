package com.opengoofy.dubos.loadblance;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XxxLoadBalance extends AbstractLoadBalance {

    // 定义一个Map，保存已经被剔除的服务提供者和剔除时间
    private static Map<String, Long> excludeMap = new ConcurrentHashMap<>();

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // 过滤已经被剔除的服务提供者
        List<Invoker<T>> filteredInvokers = filterExcludedInvokers(invokers);
        if (filteredInvokers.isEmpty()) {
            filteredInvokers = invokers;
        }
        // 调用Dubbo提供的负载均衡算法，选择一个服务提供者
        return ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("random").select(filteredInvokers, url, invocation);
    }

    // 过滤已经被剔除的服务提供者
    private <T> List<Invoker<T>> filterExcludedInvokers(List<Invoker<T>> invokers) {
        List<Invoker<T>> filteredInvokers = new ArrayList<>();
        for (Invoker<T> invoker : invokers) {
            String key = invoker.getUrl().toString();
            // 如果该服务提供者已经被剔除，并且剔除时间在30秒以内，则不选中该服务提供者
            if (!excludeMap.containsKey(key) || System.currentTimeMillis() - excludeMap.get(key) > 30000) {
                filteredInvokers.add(invoker);
            }
        }
        return filteredInvokers;
    }

    // 剔除服务提供者
    public static void exclude(String providerUrl) {
        excludeMap.put(providerUrl, System.currentTimeMillis());
    }

    // 恢复服务提供者
    public static void recover(String providerUrl) {
        excludeMap.remove(providerUrl);
    }
}
