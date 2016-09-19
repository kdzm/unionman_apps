/*
 * Copyright (C) 2008 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.um.music;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;


/**
 * ���MP3�ļ�����Ϣ
 * 
 */
public class MP3Info {

	private String charset = "utf-8";//����MP3��Ϣʱ�õ��ַ�����
	
	private byte[] buf;//MP3�ı�ǩ��Ϣ��byte����
	
	/**
	 * ʵ����һ�����MP3�ļ�����Ϣ����
	 * @param mp3 MP3�ļ�
	 * @throws IOException ��ȡMP3�������MP3�ļ�������
	 */
	public MP3Info(File mp3) throws IOException{
		
		buf = new byte[128];//��ʼ����ǩ��Ϣ��byte����
		
		RandomAccessFile raf = new RandomAccessFile(mp3, "r");//�����д��ʽ��MP3�ļ�
		raf.seek(raf.length() - 128);//�ƶ����ļ�MP3ĩβ
		raf.read(buf);//��ȡ��ǩ��Ϣ
		
		raf.close();//�ر��ļ�
		
		if(buf.length != 128){//�����Ƿ�Ϸ�
			throw new IOException("MP3��ǩ��Ϣ���ݳ��Ȳ��Ϸ�!");
		}
		
		if(!"TAG".equalsIgnoreCase(new String(buf,0,3))){//��Ϣ��ʽ�Ƿ���ȷ
			throw new IOException("MP3��ǩ��Ϣ���ݸ�ʽ����ȷ!");
		}
		
	}

	/**
	 * ���Ŀǰ����ʱ�õ��ַ�����
	 * @return Ŀǰ����ʱ�õ��ַ�����
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * ���ý���ʱ�õ��ַ�����
	 * @param charset ����ʱ�õ��ַ�����
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public String getSongName(){
		try {
			return new String(buf,3,30,charset).trim();
		} catch (UnsupportedEncodingException e) {
			return new String(buf,3,30).trim();
		}
	}
	
	public String getArtist(){
		try {
			return new String(buf,33,30,charset).trim();
		} catch (UnsupportedEncodingException e) {
			return new String(buf,33,30).trim();
		}
	}
	
	public String getAlbum(){
		try {
			return new String(buf,63,30,charset).trim();
		} catch (UnsupportedEncodingException e) {
			return new String(buf,63,30).trim();
		}
	}
	
	public String getYear(){
		try {
			return new String(buf,93,4,charset).trim();
		} catch (UnsupportedEncodingException e) {
			return new String(buf,93,4).trim();
		}
	}
	
	public String getComment(){
		try {
			return new String(buf,97,28,charset).trim();
		} catch (UnsupportedEncodingException e) {
			return new String(buf,97,28).trim();
		}
	}
	
	
}