package com.mobiletech.imageconverter.util;

public class Pixel {
	private int x = 0;
	private int y = 0;
	private int[] rgb = null;
	
	public Pixel(){}
	
	public Pixel(int x, int y, int[] rgb){
		this.x = x;
		this.y = y;
		this.rgb = rgb;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public int[] getRgb() {
		return rgb;
	}

	public void setRgb(int[] rgb) {
		this.rgb = rgb;
	}
	
	
}
