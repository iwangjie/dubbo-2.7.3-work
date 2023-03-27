package com.opengoofy.dubos.loadblance;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(group = {"consumer"})
public class XxxExceptionFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            // 调用服务提供者
            Result result = invoker.invoke(invocation);
            if (result.hasException()){
                XxxLoadBalance.exclude(invoker.getUrl().toString());
            }
            return result;
        } catch (RpcException e) {
            // 如果服务提供者出现异常，则剔除该服务提供者
            XxxLoadBalance.exclude(invoker.getUrl().toString());
            throw e;
        }
    }
}
