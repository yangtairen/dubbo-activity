/**
 * 
 */
package com.mor.server.dubbo.callback;

/** 
 * @author wanggengqi
 * @email wanggengqi@chinasofti.com
 * @date 2016年4月18日 下午4:19:11 
 */
public interface CallbackService {
	void addListener(String key, CallbackListener listener);
}
