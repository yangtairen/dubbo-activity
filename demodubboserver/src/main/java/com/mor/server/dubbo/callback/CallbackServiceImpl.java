/**
 * 
 */
package com.mor.server.dubbo.callback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 
 * @author wanggengqi
 * @email wanggengqi@chinasofti.com
 * @date 2016年4月18日 下午4:20:46 
 */
public class CallbackServiceImpl implements CallbackService{

	private final Map<String, CallbackListener> listeners = new ConcurrentHashMap<String, CallbackListener>();
	  
    public CallbackServiceImpl() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        for(Map.Entry<String, CallbackListener> entry : listeners.entrySet()){
                           try {
                               entry.getValue().changed(getChanged(entry.getKey()));  //获取CallbackListener后触发变更通知，传入参数为key
                           } catch (Throwable t) {
                               listeners.remove(entry.getKey());
                           }
                        }
                        Thread.sleep(1000); // 定时触发变更通知
                    } catch (Throwable t) { // 防御容错
                        t.printStackTrace();
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
  
    public void addListener(String key, CallbackListener listener) {
        listeners.put(key, listener);
        System.out.println("Map中存放CallbackListener的数量：" + listeners.size());
        listener.changed(getChanged(key)); // 发送变更通知
    }
     
    private String getChanged(String key) {
        return "服务端回调客户端Changed: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + key;
    }

}
