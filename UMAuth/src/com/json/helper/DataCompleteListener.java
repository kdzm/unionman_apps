package com.json.helper;

import org.apache.http.HttpEntity;

/**
 * 数据获取完成接口
 * 
 * @author Eniso
 */
public interface DataCompleteListener {

	void dataCompleteListener(HttpEntity content);
}
