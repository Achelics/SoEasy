package com.lzq.soeasy.photo;

import android.graphics.Bitmap;

public class HistogramFilter {
	
	private int redBins;
	private int greenBins;
	private int blueBins;
	private int flag;
	
	public HistogramFilter(int flag) {
		redBins = greenBins = blueBins = 4;
		this.flag = flag;
	}
	
	public void setRedBinCount(int redBinCount) {
		this.redBins = redBinCount;
	}

	public void setGreenBinCount(int greenBinCount) {
		this.greenBins = greenBinCount;
	}

	public void setBlueBinCount(int blueBinCount) {
		this.blueBins = blueBinCount;
	}
	//获取图片返回的直方图数据
	public float[] filter(Bitmap src) {
		int width = src.getWidth();
        int height = src.getHeight();
        //获取像素
        int[] inPixels = new int[width*height];
        float[] histogramData = new float[redBins * greenBins * blueBins];
        getPixels(src, 0, 0, width, height, inPixels );
        int index = 0;
        int redIdx = 0, greenIdx = 0, blueIdx = 0;
        int singleIndex = 0;
        float total = 0;
        for(int row=0; row<height; row++) {
        	@SuppressWarnings("unused")
			int ta = 0, tr = 0, tg = 0, tb = 0;
        	for(int col=0; col<width; col++) {
        		index = row * width + col;
        		ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                if(flag == 1){
                	if(tr==255 && tg==255 && tb==255){
                		continue;
                	}
                }
                redIdx = (int)getBinIndex(redBins, tr, 255);
                greenIdx = (int)getBinIndex(greenBins, tg, 255);
                blueIdx = (int)getBinIndex(blueBins, tb, 255);
                singleIndex = redIdx + greenIdx * redBins + blueIdx * redBins * greenBins;
                histogramData[singleIndex] += 1;
                total += 1;
        	}
        }
     // start to normalize the histogram data
        for (int i = 0; i < histogramData.length; i++)
        {
        	histogramData[i] = histogramData[i] / total;
        }
        
        return histogramData;
		
	}
	private float getBinIndex(int binCount, int color, int colorMaxValue) {
		float binIndex = (((float)color)/((float)colorMaxValue)) * (binCount);
		if(binIndex >= binCount)
			binIndex = binCount  - 1;
		return binIndex;
	}
	
	//将数据传入到getPixels中，进行图片处理
	public void getPixels(Bitmap image,int x, int y, int width, int height, int[] pixels){
		//重新写这个方法
		image.getPixels(pixels, 0, width, x, y, width, height);
		return;
	}

}
