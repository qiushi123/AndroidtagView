package com.pmmq.pmmqproject.ui.picture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.jimageeditdemo.R;
import com.pmmq.pmmqproject.config.Constant;
import com.pmmq.pmmqproject.ui.tag.PreViewActivity;
import com.pmmq.pmmqproject.util.FileUtils;
import com.pmmq.pmmqproject.util.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shark
 * @ClassName: TakePicActivity
 * @Description: 照片选取
 * @date 2014年11月26日 上午10:53:58
 */
public class TakePicActivity extends Activity implements OnClickListener {
    private String TAG = "TakePicActivity";
    //照片存储地址，这个照片没有添加标签
    public static final String IMAGE_SAVE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera";
    public final static String IMAGE_URI = "iamge_uri";
    public final static String CROP_IMAGE_URI = "crop_image_uri";

    public Uri mCameraImageUri;
    //	public Uri mImageUri;

    public final static int REQ_CODE_GALLERY = 201;
    public final static int REQ_CODE_CAMERA = 203;
    public final static int REQ_CODE_PHOTO_CROP = 102;

    private Button mSelectBtn;
    //	private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);


        //创建App缓存文件夹
        Constant.CACHEPATH = new FileUtils(this).makeAppDir();
        Logger.d(TAG, "Constant.CACHEPATH = " + Constant.CACHEPATH);

        Display display = getWindowManager().getDefaultDisplay();
        Constant.displayWidth = display.getWidth();
        Constant.displayHeight = display.getHeight();
        Logger.d(TAG, "Constant.displayWidth = " + Constant.displayWidth + "-- Constant.displayHeight = " + Constant.displayHeight);
        Constant.scale = getResources().getDisplayMetrics().density;
        Logger.d(TAG, "scale = " + Constant.scale);

        initViews();
    }

    private void initViews() {
        //    	mImageView = (ImageView)findViewById(R.id.jimageview);
        mSelectBtn = (Button) findViewById(R.id.selectpic);
        mSelectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.selectpic:
                showPostMenu();
                break;
            default:
                break;
        }
    }

    //显示出选择菜单
    private void showPostMenu() {
        new AlertDialog.Builder(this).setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                if (paramInt == 0) {
                    Intent localIntent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                    //设置相机图片的输出路径
                    mCameraImageUri = Uri.fromFile(new File(IMAGE_SAVE, new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg"));
                    localIntent1.putExtra("output", mCameraImageUri);
                    Logger.d(TAG, "mCameraImageUri:" + mCameraImageUri);
                    TakePicActivity.this.startActivityForResult(localIntent1, REQ_CODE_CAMERA);
                    return;
                } else {
                    Intent localIntent2 = new Intent();
                    localIntent2.setType("image/*");
                    localIntent2.setAction("android.intent.action.GET_CONTENT");
                    TakePicActivity.this.startActivityForResult(Intent.createChooser(localIntent2, "选择照片"), REQ_CODE_GALLERY);
                }
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult ");

        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_GALLERY) {
                // 从相册返回
                Uri localUri = data.getData();
                if (localUri == null) {
                    return;
                }
                readLocalImage(localUri);
            } else if (requestCode == REQ_CODE_CAMERA) {
                // 从相机返回,从设置相机图片的输出路径中提取数据
                Logger.d(TAG, "mCameraImageUri:" + mCameraImageUri);
                readLocalImage(mCameraImageUri);

            } else if (requestCode == REQ_CODE_PHOTO_CROP) {
                String imagePath = data.getStringExtra("tag_image_path");
                Intent intent = new Intent(this, PreViewActivity.class);
                intent.putExtra("tag_image_path", imagePath);
                startActivity(intent);

            }
        }
    }

    //选择照片后进行图片裁剪
    private void readLocalImage(Uri uri) {
        if (uri != null) {
            startPhotoCrop(uri, null, REQ_CODE_PHOTO_CROP); // 图片裁剪
        }
    }

    /**
     * 开始裁剪
     *
     * @param uri
     * @param duplicatePath
     * @param reqCode
     * @return void
     * @Title: startPhotoCrop
     * @date 2012-12-12 上午11:15:38
     */
    private void startPhotoCrop(Uri uri, String duplicatePath, int reqCode) {


        //		Uri duplicateUri = preCrop(uri,duplicatePath);

        Intent intent = new Intent(this, CropActivity.class);//跳转到裁剪界面
        intent.putExtra(IMAGE_URI, uri);
        startActivityForResult(intent, reqCode);

        /**intent.setDataAndType(uri, "image/*");
         intent.putExtra("crop", "true");
         intent.putExtra("outputFormat", "JPEG");
         // aspectX aspectY 是宽高的比例
         intent.putExtra("aspectX", 1);
         intent.putExtra("aspectY", 1);
         // outputX outputY 是裁剪图片宽高
         intent.putExtra("outputX", reqCode == REQ_CODE_BG_CROP ? BG_CROP_WIDTH
         : AVATAR_CROP_WIDTH);
         intent.putExtra("outputY", reqCode == REQ_CODE_BG_CROP ? BG_CROP_HEIGHT
         : AVATAR_CROP_HEIGHT);
         intent.putExtra(MediaStore.EXTRA_OUTPUT, duplicateUri);
         intent.putExtra("return-data", true);*/

    }

}
