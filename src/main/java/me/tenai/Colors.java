package me.tenai;

import java.math.*;

public final class Colors {
    public static final int MIN_HUE = 0;
    public static final int MAX_HUE = 63;
    public static final int MIN_SATURATION = 0;
    public static final int MAX_SATURATION = 7;
    public static final int MIN_LIGHTNESS = 0;
    public static final int MAX_LIGHTNESS = 127;

    public static final int MIN_HSL = packJagexHsl(MIN_HUE, MIN_SATURATION, MIN_LIGHTNESS);
    public static final int MAX_HSL = packJagexHsl(MAX_HUE, MAX_SATURATION, MAX_LIGHTNESS);

    Colors() {}

    public static int[] getUnpackedJagexHsl(int jagexHsl) {
        int hue = unpackJagexHue(jagexHsl);
        int saturation = unpackJagexSaturation(jagexHsl);
        int lightness = unpackJagexLightness(jagexHsl);
        return new int[] { hue, saturation, lightness };
    }

    public static int packJagexHsl(int hue, int saturation, int lightness) {
        return hue << 10 | saturation << 7 | lightness;
    }

    public static int unpackJagexHue(int jagexHsl) {
        return jagexHsl >> 10 & 0x3F;
    }

    public static int unpackJagexSaturation(int jagexHsl) {
        return jagexHsl >> 7 & 7;
    }

    public static int unpackJagexLightness(int jagexHsl) {
        return jagexHsl & 0x7F;
    }
    
    public static int rgbToHSL(int rgb) {
		 float r = ((0xff0000 & rgb) >> 16) / 255.f;
		 float g = ((0x00ff00 & rgb) >> 8) / 255.f;
		 float b = ((0x0000ff & rgb)) / 255.f;
		 float max = Math.max(Math.max(r, g), b);
		 float min = Math.min(Math.min(r, g), b);
		 float c = max - min;
		 
		 float h_ = 0.f;
		 if (c == 0) {
		  h_ = 0;
		 } else if (max == r) {
		  h_ = (float)(g-b) / c;
		  if (h_ < 0) h_ += 6.f;
		 } else if (max == g) {
		  h_ = (float)(b-r) / c + 2.f;
		 } else if (max == b) {
		  h_ = (float)(r-g) / c + 4.f;
		 }
		 float h = 60.f * h_;
		 
		 float l = (max + min) * 0.5f;
		 
		 float s;
		 if (c == 0) {
		  s = 0.f;
		 } else {
		  s = c / (1 - Math.abs(2.f * l - 1.f));
		 }
		 
		 int hue = (int) Math.floor(h / 360.0 * 63.0);
		 int sat = (int) Math.floor(Math.floor(s*1000)/10.0 / 100.0 * 7);
		 int lum = (int) Math.floor(Math.floor(l*1000)/10.0 / 100.0 * 127);
		 
		 if(lum >= 128) lum = 127;
		 
		 return packJagexHsl(hue, sat, lum);
    }
}