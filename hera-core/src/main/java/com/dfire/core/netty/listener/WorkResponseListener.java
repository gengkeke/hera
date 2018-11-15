package com.dfire.core.netty.listener;

import com.dfire.core.netty.listener.adapter.ResponseListenerAdapter;
import com.dfire.core.netty.worker.WorkContext;
import com.dfire.logs.TaskLog;
import com.dfire.protocol.RpcWebRequest;
import com.dfire.protocol.RpcWebResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;

/**
 * @author xiaosuda
 * @date 2018/7/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WorkResponseListener extends ResponseListenerAdapter {


    private RpcWebRequest.WebRequest request;
    private WorkContext workContext;
    private Boolean receiveResult;
    private CountDownLatch latch;
    private RpcWebResponse.WebResponse webResponse;

    @Override
    public void onWebResponse(RpcWebResponse.WebResponse response) {
        if (request.getRid() == response.getRid()) {
            try {
                TaskLog.info("work release lock for request :{}", response.getRid());
                workContext.getHandler().removeListener(this);
                webResponse = response;
                receiveResult = true;
            } catch (Exception e) {
                TaskLog.error("work release exception {}", e);
            } finally {
                latch.countDown();
            }
        }
    }
}
