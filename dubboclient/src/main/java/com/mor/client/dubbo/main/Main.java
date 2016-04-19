package com.mor.client.dubbo.main;

import com.mor.client.dubbo.action.ChatAction;

public class Main {

    public static void main(String[] args) throws InterruptedException {
    	int i=0;
    	System.out.println("Start.......");
    	ChatAction act = new ChatAction();
    	while(true){
    		act.TestCallback();
//    		act.SayHello();
    		Thread.sleep(5000);
    	}
    }

}
