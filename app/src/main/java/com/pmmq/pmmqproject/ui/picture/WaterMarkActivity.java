package com.pmmq.pmmqproject.ui.picture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.jimageeditdemo.R;
import com.pmmq.pmmqproject.config.Constant;
import com.pmmq.pmmqproject.ui.tag.AddTagActivity;
import com.pmmq.pmmqproject.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author shark
 * @ClassName: WaterMarkActivity
 * @Description: 添加水印，有两种水印方式：1.与目标图片叠加（布局中已隐藏）	2.贴纸形式，可以移动、缩放、旋转
 * @date 2014年11月26日 上午10:52:45
 */
public class WaterMarkActivity extends Activity implements OnClickListener {

    private String TAG = "WaterMarkActivity";

    public final static int REQ_CODE = 211;

    private RelativeLayout mRootLayout;
    private ImageView mImageView;
    private ImageView mWatermark1;
    private ImageView mWatermark2;
    private ImageView mWatermark3;
    private Button mNextBtn;
    private ImageView mWatermark4;
    private ImageView mWatermark5;
    private ImageView mWatermark6;

    private final Handler mHandler = new Handler();

    private Uri mImageUri;            //目标图片的Uri
    private String mImagePath;        //目标图片的路径

    private TouchImageView mWaterMarkView = null;

    private int[] mMarkRes = {
            R.drawable.wm_1,
            R.drawable.wm_2,
            R.drawable.wm_3
    };

    private int[] mMarkRes_2 = {
            R.drawable.wm_5,
            R.drawable.wm_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark);

        //裁剪后传过来的图片路径
        mImagePath = getIntent().getStringExtra(TakePicActivity.CROP_IMAGE_URI);
        //裁剪后的uri
        mImageUri = Uri.parse(mImagePath);
        Logger.w(TAG, "imagePath:" + mImagePath);
        Logger.w(TAG, "mImageUri:" + mImageUri);
        initView();

    }

    private void initView() {
        mRootLayout = (RelativeLayout) findViewById(R.id.wm_image_layout);
        mImageView = (ImageView) findViewById(R.id.wm_image);

        int width = getWindowManager().getDefaultDisplay().getWidth();//获取屏幕高度
        //设置图片显示宽高为屏幕宽度
        LayoutParams params = new LayoutParams(width, width);
        mImageView.setLayoutParams(params);
        if (mImageUri != null) {
            mImageView.setImageURI(mImageUri);
        }

        mWatermark1 = (ImageView) findViewById(R.id.wm_mark1);
        mWatermark2 = (ImageView) findViewById(R.id.wm_mark2);
        mWatermark3 = (ImageView) findViewById(R.id.wm_mark3);
        mWatermark1.setOnClickListener(this);
        mWatermark2.setOnClickListener(this);
        mWatermark3.setOnClickListener(this);

        mWatermark4 = (ImageView) findViewById(R.id.wm_mark4);
        mWatermark5 = (ImageView) findViewById(R.id.wm_mark5);
        mWatermark6 = (ImageView) findViewById(R.id.wm_mark6);
        mWatermark4.setOnClickListener(this);
        mWatermark5.setOnClickListener(this);
        mWatermark6.setOnClickListener(this);

        mNextBtn = (Button) findViewById(R.id.wm_next_btn);
        mNextBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.wm_mark1:            //水印方式1，隐藏中
                addWaterMark(0);
                break;
            case R.id.wm_mark2:
                addWaterMark(1);
                break;
            case R.id.wm_mark3:
                addWaterMark(2);
                break;
            case R.id.wm_mark4:
                addWaterMark_2(10);        //空  ，不添加水印
                break;
            case R.id.wm_mark5:
                addWaterMark_2(0);        //水印方式2
                break;
            case R.id.wm_mark6:
                addWaterMark_2(1);
                break;
            case R.id.wm_next_btn:
                goToNextActivity();
                break;
            default:
                break;
        }
    }

    //添加水印贴纸，可以移动、缩放、旋转
    private void addWaterMark_2(int i) {

        if (i > mMarkRes_2.length) {    //空  ，不添加水印
            if (mWaterMarkView != null) {
                mRootLayout.removeView(mWaterMarkView);
                mWaterMarkView = null;
            }
        } else {
            if (mWaterMarkView != null) {
                mRootLayout.removeView(mWaterMarkView);
            }
            //设置图片显示宽高为屏幕宽度
            LayoutParams params = new LayoutParams(Constant.displayWidth, Constant.displayWidth);

            Bitmap watermark = BitmapFactory.decodeResource(getResources(), mMarkRes_2[i]);

            int ww = watermark.getWidth();
            int wh = watermark.getHeight();
            Logger.w(TAG, "watermark ww:" + ww);
            Logger.w(TAG, "watermark wh:" + wh);

            //如果水印图片太大则压缩
            if (ww > Constant.displayWidth || wh > Constant.displayWidth) {
                // 缩放图片的尺寸
                float scaleWidth = (float) Constant.displayWidth / ww;
                float scaleHeight = (float) Constant.displayWidth / wh;
                float scale = Math.min(scaleWidth, scaleHeight) * (float) 0.8;    //屏幕宽度的80%
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                // 产生缩放后的Bitmap对象
                watermark = Bitmap.createBitmap(watermark, 0, 0, ww, wh, matrix, false);
            }

            TouchImageView touchImageView = new TouchImageView(this, watermark);
            mRootLayout.addView(touchImageView, params);
            mWaterMarkView = touchImageView;
        }
    }

    /**
     * 从TouchImageView中获取图片Bitmap
     *
     * @return Bitmap
     */
    private Bitmap getWaterMarkImage() {
        Bitmap photo = BitmapFactory.decodeFile(mImagePath);
        if (mWaterMarkView != null) {
            Bitmap mark = mWaterMarkView.CreatNewPhoto();
            Bitmap a = createBitmap(photo, mark);

            photo.recycle();
            mark.recycle();
            return a;
        } else {
            return photo;
        }

    }

    //添加水印,方式1，图片叠加
    private void addWaterMark(int i) {
        Bitmap photo = BitmapFactory.decodeFile(mImagePath);
        Bitmap mark = BitmapFactory.decodeResource(this.getResources(), mMarkRes[i]);
        Bitmap a = createBitmap(photo, mark);
        mImageView.setImageBitmap(a);
        photo.recycle();
        mark.recycle();
        //		a.recycle();
        //		saveMyBitmap(a);
    }

    /**
     * @param src
     * @param watermark
     * @return
     */
    private Bitmap createBitmap(Bitmap src, Bitmap watermark) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        Logger.d(TAG, "createBitmap w:" + w);
        Logger.d(TAG, "createBitmap h:" + h);
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        Logger.d(TAG, "watermark ww:" + ww);
        Logger.d(TAG, "watermark wh:" + wh);

        // 缩放图片的尺寸
        float scaleWidth = (float) w / ww;
        float scaleHeight = (float) h / wh;
        Logger.d(TAG, "watermark scaleWidth:" + scaleWidth);
        Logger.d(TAG, "watermark scaleHeight:" + scaleHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 产生缩放后的Bitmap对象  
        Bitmap resizeBitmap = Bitmap.createBitmap(watermark, 0, 0, ww, wh, matrix, false);
        Logger.d(TAG, "resizeBitmap ww:" + resizeBitmap.getWidth());
        Logger.d(TAG, "resizeBitmap wh:" + resizeBitmap.getHeight());


        //create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        //创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        //draw src into
        cv.drawBitmap(src, 0, 0, null);//在 0，0坐标开始画入src
        //draw watermark into
        cv.drawBitmap(resizeBitmap, 0, 0, null);//在src的右下角画入水印
        //save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        //store
        cv.restore();//存储

        resizeBitmap.recycle();
        return newb;
    }

    /**
     * 将Bitmap放入缓存，
     *
     * @param bitmap
     * @param filePath
     * @return void
     * @Title: saveDrawableToCache
     * @date 2012-12-14 上午9:27:38
     */
    private void saveDrawableToCache(Bitmap bitmap, String filePath) {

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

    /**
     * 下一步
     */
    private void goToNextActivity() {
        //保存图片
        //    	final Bitmap photo = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();

        final Bitmap photo = getWaterMarkImage();

        final String wmImagePath = mImagePath + "_water_mark";
        Logger.w(TAG, "goToNextActivity wmImagePath:" + wmImagePath);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                saveDrawableToCache(photo, wmImagePath);
                //		    	photo.recycle();
            }

        });

        Intent intent = new Intent(this, AddTagActivity.class);
        intent.putExtra(TakePicActivity.CROP_IMAGE_URI, wmImagePath);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult ");

        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE) {
                String imagePath = data.getStringExtra("tag_image_path");
                Intent intent = new Intent();
                intent.putExtra("tag_image_path", imagePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
