package com.pmmq.pmmqproject.ui.picture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jimageeditdemo.R;
import com.example.jimageeditdemo.api.BitmapFilter;
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
public class WaterMarkActivity_ImageFilter extends Activity implements OnClickListener {
    private Bitmap originalBitmap;
    private Bitmap sBitmap;
    private Button original_btn, button_change_to_gray, button_change_to_old,
            button_change_to_ice, button_change_to_carton, button_change_to_soft,
            button_change_to_eclosion, button_change_to_light, button_change_to_haha;

    private ImageView mImageView;//显示图片的
    private Button mNextBtn;//下一步

    private String TAG = "WaterMarkActivity";

    public final static int REQ_CODE = 211;

    private final Handler mHandler = new Handler();

    private Uri mImageUri;            //目标图片的Uri
    private String mImagePath;        //目标图片的路径

    private TouchImageView mWaterMarkView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark_image_filter);

        //裁剪后传过来的图片路径
        mImagePath = getIntent().getStringExtra(TakePicActivity.CROP_IMAGE_URI);
        //裁剪后的uri
        mImageUri = Uri.parse(mImagePath);
        originalBitmap = BitmapFactory.decodeFile(mImagePath);
        initView();

    }

    private void initView() {
        findViewById(R.id.original_btn).setOnClickListener(this);//原始照片

        original_btn = (Button) findViewById(R.id.original_btn);
        button_change_to_gray = (Button) findViewById(R.id.button_change_to_gray);
        button_change_to_old = (Button) findViewById(R.id.button_change_to_old);
        button_change_to_ice = (Button) findViewById(R.id.button_change_to_ice);
        button_change_to_carton = (Button) findViewById(R.id.button_change_to_carton);
        button_change_to_soft = (Button) findViewById(R.id.button_change_to_soft);
        button_change_to_eclosion = (Button) findViewById(R.id.button_change_to_eclosion);
        button_change_to_light = (Button) findViewById(R.id.button_change_to_light);
        button_change_to_haha = (Button) findViewById(R.id.button_change_to_haha);

        original_btn.setOnClickListener(this);
        button_change_to_gray.setOnClickListener(this);
        button_change_to_old.setOnClickListener(this);
        button_change_to_ice.setOnClickListener(this);
        button_change_to_carton.setOnClickListener(this);
        button_change_to_soft.setOnClickListener(this);
        button_change_to_eclosion.setOnClickListener(this);
        button_change_to_light.setOnClickListener(this);
        button_change_to_haha.setOnClickListener(this);

        //        originalBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.huishu);

        mImageView = (ImageView) findViewById(R.id.wm_image);

        int width = getWindowManager().getDefaultDisplay().getWidth();//获取屏幕高度
        //设置图片显示宽高为屏幕宽度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        mImageView.setLayoutParams(params);
        mImageView.setImageBitmap(originalBitmap);
        //        if (mImageUri != null) {
        //            mImageView.setImageURI(mImageUri);
        //        }
        mNextBtn = (Button) findViewById(R.id.wm_next_btn);
        mNextBtn.setOnClickListener(this);

        initFilter();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initFilter() {
        //无滤镜效果
        Drawable bd = new BitmapDrawable(getResources(), originalBitmap);
        original_btn.setBackground(bd);

        //黑白效果
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.GRAY_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_gray.setBackground(bd);


        //怀旧效果
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.OLD_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_old.setBackground(bd);

        //冰冻效果
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.ICE_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_ice.setBackground(bd);

        //连环画效果
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.CARTON_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_carton.setBackground(bd);

        //柔滑美白
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.SOFTNESS_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_soft.setBackground(bd);

        //羽化效果
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.ECLOSION_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_eclosion.setBackground(bd);

        //光照效果
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.LIGHT_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_light.setBackground(bd);
        //哈哈镜效果
        sBitmap = BitmapFilter.changeStyle(originalBitmap, BitmapFilter.HAHA_STYLE);
        bd = new BitmapDrawable(getResources(), sBitmap);
        button_change_to_haha.setBackground(bd);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sBitmap != null) {
            sBitmap.recycle();
            sBitmap = null;
        }
    }

    int styleNo = BitmapFilter.GRAY_STYLE;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        //        if (sBitmap != null) {
        //            sBitmap.recycle();
        //            sBitmap = null;
        //        }

        switch (v.getId()) {
            case R.id.wm_next_btn:
                goToNextActivity();
                break;
            case R.id.original_btn:
                mImageView.setImageBitmap(originalBitmap);
                return;
            case R.id.button_change_to_gray:
                styleNo = BitmapFilter.GRAY_STYLE;
                break;

            case R.id.button_change_to_old:
                styleNo = BitmapFilter.OLD_STYLE;
                break;
            case R.id.button_change_to_ice:
                styleNo = BitmapFilter.ICE_STYLE;
                break;
            case R.id.button_change_to_carton:
                //连环画效果
                styleNo = BitmapFilter.CARTON_STYLE;
                break;

            case R.id.button_change_to_soft:
                //羽化效果
                styleNo = BitmapFilter.SOFTNESS_STYLE;
                break;
            case R.id.button_change_to_eclosion:
                styleNo = BitmapFilter.ECLOSION_STYLE;
                break;
            case R.id.button_change_to_light:
                styleNo = BitmapFilter.LIGHT_STYLE;
                break;
            case R.id.button_change_to_haha:
                //哈哈镜效果
                styleNo = BitmapFilter.HAHA_STYLE;
                break;
            //下面几个效果暂时不用
            //            case R.id.button_change_to_block:
            //                //版画效果
            //                styleNo = BitmapFilter.BLOCK_STYLE;
            //                break;
            //            case R.id.button_change_to_oid:
            //                //油画效果
            //                styleNo = BitmapFilter.OIL_STYLE;
            //                break;
            //            case R.id.button_change_to_molten:
            //                //铸融效果
            //                styleNo = BitmapFilter.MOLTEN_STYLE;
            //                break;
            //            case R.id.button_change_to_invert:
            //                //反色效果
            //                styleNo = BitmapFilter.INVERT_STYLE;
            //                break;
            //            case R.id.button_change_to_relief:
            //                //浮雕
            //                styleNo = BitmapFilter.RELIEF_STYLE;
            //                break;

        }
        sBitmap = BitmapFilter.changeStyle(originalBitmap, styleNo);
        mImageView.setImageBitmap(sBitmap);
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

        //        final Bitmap photo = getWaterMarkImage();

        final String wmImagePath = mImagePath + "_water_mark";
        Logger.w(TAG, "goToNextActivity wmImagePath:" + wmImagePath);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                //                saveDrawableToCache(photo, wmImagePath);
                saveDrawableToCache(sBitmap, wmImagePath);
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
                originalBitmap = BitmapFactory.decodeFile(imagePath);
                initView();
                Intent intent = new Intent();
                intent.putExtra("tag_image_path", imagePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }


}
