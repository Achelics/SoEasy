package com.lzq.soeasy.photo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CompressPhoto {
	
	//ѹ��ͼƬ����ͼƬ�Ĵ�Сѹ����100kb����
		public static Bitmap compressImage(Bitmap image) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
			int options = 100;
			while ( baos.toByteArray().length / 1024 > 30) {	//ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��		
				baos.reset();//����baos�����baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
				options -= 5;//ÿ�ζ�����5
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
			return bitmap;
		}
	
}
