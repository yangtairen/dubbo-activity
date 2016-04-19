package com.mor.client.dubbo.action;


import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mor.client.callback.CallbackListenerImpl;
import com.mor.server.dubbo.callback.CallbackListener;
import com.mor.server.dubbo.callback.CallbackService;
import com.mor.server.dubbo.service.DemoServer;

public class ChatAction {
    /**
     * 
     * @author wanggengqi
     * @date 2014年10月23日 下午3:13:04
     */
	
	ClassPathXmlApplicationContext context=null;
	
	public ChatAction() {
		// TODO Auto-generated constructor stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationConsumer.xml" });
		context.start();
	}
	
    public void SayHello(){ 
//    	if(context==null){
//    		context = new ClassPathXmlApplicationContext(new String[] { "applicationConsumer.xml" });
//    		context.start();
//    	}
    	DemoServer demoServer = (DemoServer) context.getBean("demoService");
		System.out.println("#####client:"+demoServer.sayHello(new Date() + ""));
		
//		CallbackService callbackService = (CallbackService) context.getBean("callbackService");
//		
//		System.out.println("##########client： Callback##########");
//		
//		callbackService.addListener("http://10.20.160.198/wiki/display/dubbo/foo.bar", new CallbackListener(){
//		    public void changed(String msg) {
//		        System.out.println("callback------->" + msg);
//		    }
//		});
//		
//		System.out.println("##########client： Callback END##########");
    }
    
    public void TestCallback(){
    	if(context==null){
			context = new ClassPathXmlApplicationContext(new String[] { "applicationConsumer.xml" });
			context.start();
    	}
    	
    	CallbackService callbackService = (CallbackService) context.getBean("callbackService");
    	
    	if(callbackService==null){
    		System.out.println("获取callback bean 失败");
    	}
    	CallbackListener callbackListener = new CallbackListenerImpl();
    	
		callbackService.addListener("idCallback", callbackListener);
    }

}
