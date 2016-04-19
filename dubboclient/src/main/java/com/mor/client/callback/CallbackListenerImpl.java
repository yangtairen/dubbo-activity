package com.mor.client.callback;

import com.mor.server.dubbo.callback.CallbackListener;

public class CallbackListenerImpl implements CallbackListener{

	public void changed(String arg0) {
		System.out.println("client was called back " + arg0);
		
	}

}
