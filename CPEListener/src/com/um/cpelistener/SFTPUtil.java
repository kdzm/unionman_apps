package com.um.cpelistener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
*/
public class SFTPUtil {
	private String TAG="CpeListener--ListenService--SFTPUtil";
	private String filePath="";
	/**
	 * 连接sftp服务器
	 * 
	 * @param host
	 *            主机
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	public ChannelSftp connect(String host, int port, String username,
			String password,String filePath) {
		this.filePath=filePath;
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			Log.i(TAG, "Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Log.i(TAG, "Session connected.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			Log.i(TAG, "Connected to " + host + ".");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sftp;
	}

	/**
	 * 上传文件
	 * 
	 * @param directory
	 *            上传的目录
	 * @param uploadFile
	 *            要上传的文件
	 * @param sftp
	 */
	public void upload(ChannelSftp sftp) {
		Log.i(TAG, "upload");
		try {
			//sftp.cd(directory);
			File file = new File(filePath);
			if(file.exists()){
				Log.i(TAG, filePath+" is exists,upload file");
				sftp.put(new FileInputStream(file), file.getName());
				file.delete();
			}else{
				Log.i(TAG, filePath+" is not exists");
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 * @param sftp
	 */
	public void download(String directory, String downloadFile,
			String saveFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(saveFile);
			sftp.get(downloadFile, new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 * @param sftp
	 */
	public void delete(String directory, String deleteFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}