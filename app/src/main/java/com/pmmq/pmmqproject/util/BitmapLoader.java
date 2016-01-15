package com.pmmq.pmmqproject.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitmapLoader
{
/*  public static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    int i = paramOptions.outHeight;
    int j = paramOptions.outWidth;
    int k = 1;
    if ((i > paramInt2) || (j > paramInt1))
    {
      int l = i / 2;
      int i1 = j / 2;
      while ((l / k > paramInt2) && (i1 / k > paramInt1))
        k *= 2;
    }
    return k;
  }*/
  /**
  * 计算压缩比例值
  * @param options       解析图片的配置信息
  * @param reqWidth            所需图片压缩尺寸最小宽度
  * @param reqHeight           所需图片压缩尺寸最小高度
  * @return
  */
  public static int calculateInSampleSize(BitmapFactory.Options options,
               int reqWidth, int reqHeight) {
         // 保存图片原宽高值
         final int height = options. outHeight;
         final int width = options. outWidth;
         // 初始化压缩比例为1
         int inSampleSize = 1;

         // 当图片宽高值任何一个大于所需压缩图片宽高值时,进入循环计算系统
         if (height > reqHeight || width > reqWidth) {

               final int halfHeight = height / 2;
               final int halfWidth = width / 2;

               // 压缩比例值每次循环两倍增加,
               // 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
               while ((halfHeight / inSampleSize) >= reqHeight
                          && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
              }
        }

         return inSampleSize;
  }
  
/*
  private static String dec2DMS(double paramDouble)
  {
    if (paramDouble > 0.0D);
    while (true)
    {
      String str1 = Integer.toString((int)paramDouble) + "/1,";
      double d1 = 60.0D * (paramDouble % 1.0D);
      String str2 = str1 + Integer.toString((int)d1) + "/1,";
      double d2 = 60000.0D * (d1 % 1.0D);
      return str2 + Integer.toString((int)d2) + "/1000";
      paramDouble = -paramDouble;
    }
  }*/
  
  /**
  * 获取压缩后的图片
  * @param res
  * @param resId
  * @param reqWidth            所需图片压缩尺寸最小宽度
  * @param reqHeight           所需图片压缩尺寸最小高度
  * @return
  */
  public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
          int reqWidth, int reqHeight) {
     
      // 首先不加载图片,仅获取图片尺寸
      final BitmapFactory.Options options = new BitmapFactory.Options();
      // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
      options.inJustDecodeBounds = true;
      // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
      BitmapFactory.decodeResource(res, resId, options);

      // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
      options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

      // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
      options. inJustDecodeBounds = false;
      // 利用计算的比例值获取压缩后的图片对象
      return BitmapFactory.decodeResource(res, resId, options);
  }
  

  public static Bitmap decodeSampledBitmapFromUri(ContentResolver paramContentResolver, Uri paramUri, int paramInt1, int paramInt2)
  {
    BitmapFactory.Options localOptions = getBitmapOptions(paramContentResolver, paramUri);
    Bitmap localObject = null;
    if (localOptions != null)
    {
      localOptions.inSampleSize = calculateInSampleSize(localOptions, paramInt1, paramInt2);
      localOptions.inJustDecodeBounds = false;
    }
    try
    {
      Bitmap localBitmap = BitmapFactory.decodeStream(paramContentResolver.openInputStream(paramUri), null, localOptions);
      localObject = localBitmap;
      return localObject;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      localFileNotFoundException.printStackTrace();
    }
    return null;
  }

  private static double dms2Dbl(String paramString)
  {
    double d1 = 999.0D;
    try
    {
      String[] arrayOfString1 = paramString.split(",", 3);
      String[] arrayOfString2 = arrayOfString1[0].split("/", 2);
      d1 = new Double(arrayOfString2[0]).doubleValue() / new Double(arrayOfString2[1]).doubleValue();
      String[] arrayOfString3 = arrayOfString1[1].split("/", 2);
      d1 += new Double(arrayOfString3[0]).doubleValue() / new Double(arrayOfString3[1]).doubleValue() / 60.0D;
      String[] arrayOfString4 = arrayOfString1[2].split("/", 2);
      double d2 = new Double(arrayOfString4[0]).doubleValue();
      double d3 = new Double(arrayOfString4[1]).doubleValue();
      return d1 + d2 / d3 / 3600.0D;
    }
    catch (Exception localException)
    {
    }
    return d1;
  }

  /*public static Location exif2Loc(String paramString)
  {
    Location localLocation = new Location("exif");
    int i = 1;
    String str1 = "";
    String str2 = "";
    double d1 = 0.0D;
    double d2 = 0.0D;
    try
    {
      ExifInterface localExifInterface = new ExifInterface(paramString);
      String str3 = localExifInterface.getAttribute("GPSLatitude");
      String str4 = localExifInterface.getAttribute("GPSLongitude");
      str1 = localExifInterface.getAttribute("GPSLatitudeRef");
      str2 = localExifInterface.getAttribute("GPSLongitudeRef");
      d1 = dms2Dbl(str3);
      if (d1 > 180.0D)
        i = 0;
      double d3 = dms2Dbl(str4);
      d2 = d3;
      if (d2 > 180.0D)
        i = 0;
      label110: if (i == 0)
        break label164;
      if (str1.contains("S"))
        d1 = -d1;
      if (str2.contains("W"))
        d2 = -d2;
      localLocation.setLatitude(d1);
      localLocation.setLongitude(d2);
      label164: return localLocation;
    }
    catch (IOException localIOException)
    {
      i = 0;
      break ;
      localLocation.setLatitude(Constant.GPS.lat);
      localLocation.setLongitude(Constant.GPS.lng);
    }
    return localLocation;
  }
*/
  public static BitmapFactory.Options getBitmapOptions(ContentResolver paramContentResolver, Uri paramUri)
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    try
    {
      BitmapFactory.decodeStream(paramContentResolver.openInputStream(paramUri), null, localOptions);
      return localOptions;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      localFileNotFoundException.printStackTrace();
    }
    return null;
  }

  private static int getExifOrientation(Uri paramUri)
  {
    try
    {
      int i = new ExifInterface(paramUri.getPath()).getAttributeInt("Orientation", 1);
      return i;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return 1;
  }

/*  public static void loadFile(Context paramContext, String paramString1, String paramString2, ImageView paramImageView)
  {
    boolean bool = TextUtils.isEmpty(paramString1);
    int i = 0;
    if (bool);
    while (i != 0)
    {
      paramImageView.setImageResource(i);
      return;
      i = paramContext.getResources().getIdentifier(paramString1.substring(0, paramString1.indexOf(".")), "drawable", paramContext.getPackageName());
    }
    MatchaLoader.displayImage(paramString2, paramImageView);
  }*/

  /*public static void loc2Exif(Location paramLocation, String paramString)
  {
    if ((paramLocation != null) && (!TextUtils.isEmpty(paramString)));
    try
    {
      ExifInterface localExifInterface = new ExifInterface(paramString);
      localExifInterface.setAttribute("GPSLatitude", dec2DMS(paramLocation.getLatitude()));
      localExifInterface.setAttribute("GPSLongitude", dec2DMS(paramLocation.getLongitude()));
      if (paramLocation.getLatitude() > 0.0D)
      {
        localExifInterface.setAttribute("GPSLatitudeRef", "N");
        label63: if (paramLocation.getLongitude() <= 0.0D)
          break label96;
        localExifInterface.setAttribute("GPSLongitudeRef", "E");
      }
      while (true)
      {
        localExifInterface.saveAttributes();
        return;
        localExifInterface.setAttribute("GPSLatitudeRef", "S");
        break ;
        label96: localExifInterface.setAttribute("GPSLongitudeRef", "W");
      }
      return;
    }
    catch (IOException localIOException)
    {
    }
  }*/
/*
  public static void loc2Exif(String paramString)
  {
    Location localLocation = new Location("exif");
    localLocation.setLatitude(Constant.GPS.lat);
    localLocation.setLongitude(Constant.GPS.lng);
    loc2Exif(localLocation, paramString);
  }*/
}