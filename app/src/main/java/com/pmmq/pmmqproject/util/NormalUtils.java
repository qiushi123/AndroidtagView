package com.pmmq.pmmqproject.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.text.TextPaint;

public class NormalUtils {

	/**
	 * 获取字符串显示宽度，与字体有关
	 * @param text
	 * @param Size
	 * @return
	 */
	public static float GetTextWidth(String text, float Size) { 
		//第一个参数是要计算的字符串，第二个参数是字提大小
		TextPaint FontPaint = new TextPaint();
		FontPaint.setTextSize(Size);
		return FontPaint.measureText(text);
	}
	
	/**
     * 将Bitmap放入缓存，
     * @Title: saveDrawableToCache
     * @param bitmap
     * @param filePath
     * @return void
     * @date 2012-12-14 上午9:27:38
     */
	public void saveDrawableToCache(Bitmap bitmap, String filePath){
		
		try {
			File file = new File(filePath);
			
			file.createNewFile();
			
			OutputStream outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream); 
			outStream.flush();
			outStream.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
