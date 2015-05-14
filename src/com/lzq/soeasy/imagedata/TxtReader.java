package com.lzq.soeasy.imagedata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TxtReader {
	/**
	 * 通过一个InputStream获取内容
	 * 
	 * */
	
	public static String getString(InputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	/**
	 * 通过txt文件的路径获取内容
	 * */
	public static String getString(String filePath){
		File file = new File(filePath);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return getString(fileInputStream);
		
	}

}
