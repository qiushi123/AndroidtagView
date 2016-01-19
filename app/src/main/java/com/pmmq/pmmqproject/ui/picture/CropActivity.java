package com.pmmq.pmmqproject.ui.picture;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.jimageeditdemo.R;
import com.pmmq.pmmqproject.config.Constant;
import com.pmmq.pmmqproject.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 图片裁剪界面
 *
 * @author shark
 * @ClassName: CropActivity
 * @Description: The activity can crop specific region of interest from an image.图片裁剪界面
 * @date 2014年11月26日 上午10:53:25
 */
public class CropActivity extends MonitoredActivity {

    private String TAG = "CropActivity";

    public final static int REQ_CODE = 211;

    public Boolean mSaving = false;

    private int mAspectX, mAspectY;
    private final Handler mHandler = new Handler();

    private boolean mCircleCrop = false;

    private CropImageView mImageView;

    private Bitmap mBitmap;

    //private RotateBitmap rotateBitmap;
    HighlightView mCrop;

    Uri targetUri;

    HighlightView hv;

    private ContentResolver mContentResolver;

    private static final int DEFAULT_WIDTH = 720;
    private static final int DEFAULT_HEIGHT = 1280;

    private int width;
    private int height;
    private int sampleSize = 1;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop);

        initViews();

        //        获取传递过来的图片uri
        Intent intent = getIntent();
        targetUri = intent.getParcelableExtra(TakePicActivity.IMAGE_URI);
        mContentResolver = getContentResolver();

        boolean isBitmapRotate = false;
        if (mBitmap == null) {
            String path = getFilePath(targetUri);
            //判断图片是不是旋转了90度，是的话就进行纠正。
            isBitmapRotate = isRotateImage(path);

            getBitmapSize();
            getBitmap();
        }

        if (mBitmap == null) {
            finish();
            return;
        }

        startFaceDetection(isBitmapRotate);
    }

    /**
     * 此处写方法描述
     *
     * @return void
     * @Title: initViews
     * @date 2012-12-14 上午10:41:23
     */
    private void initViews() {
        mImageView = (CropImageView) findViewById(R.id.image);
        mImageView.mContext = this;

        findViewById(R.id.cr_cancel).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //取消
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
        findViewById(R.id.cr_rotate).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //旋转
                onRotateClicked();
            }
        });

        findViewById(R.id.cr_next).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //下一步  先保存图片
                onSaveClicked();
            }
        });
    }

    /**
     * 获取Bitmap分辨率，太大了就进行压缩
     *
     * @return void
     * @Title: getBitmapSize
     * @date 2012-12-14 上午8:32:13
     */
    private void getBitmapSize() {
        InputStream is = null;
        try {

            is = getInputStream(targetUri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);

            width = options.outWidth;
            height = options.outHeight;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 此处写方法描述
     *
     * @param
     * @return void
     * @Title: getBitmap
     * @date 2012-12-13 下午8:22:23
     */
    private void getBitmap() {
        InputStream is = null;
        try {

            try {
                is = getInputStream(targetUri);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //shark 如果图片太大的话，压缩
            while ((width / sampleSize > DEFAULT_WIDTH * 2) || (height / sampleSize > DEFAULT_HEIGHT * 2)) {
                sampleSize *= 2;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;

            mBitmap = BitmapFactory.decodeStream(is, null, options);

            // 缩放图片的尺寸  
            int ww = mBitmap.getWidth();
            int wh = mBitmap.getHeight();
            float scaleWidth = 0;
            if (ww > wh) {
                scaleWidth = (float) Constant.IMAGEWIDTH / wh;
            } else {
                scaleWidth = (float) Constant.IMAGEWIDTH / ww;
            }

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleWidth);
            // 产生缩放后的Bitmap对象  
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, ww, wh, matrix, false);

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 此处写方法描述
     *
     * @param path
     * @return void
     * @Title: rotateImage
     * @date 2012-12-14 上午10:58:26
     */
    private boolean isRotateImage(String path) {

        try {
            ExifInterface exifInterface = new ExifInterface(path);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取输入流
     *
     * @param mUri
     * @return InputStream
     * @Title: getInputStream
     * @date 2012-12-14 上午9:00:31
     */
    private InputStream getInputStream(Uri mUri) throws IOException {
        try {
            if (mUri.getScheme().equals("file")) {
                return new java.io.FileInputStream(mUri.getPath());
            } else {
                return mContentResolver.openInputStream(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    /**
     * 根据Uri返回文件路径
     *
     * @param mUri
     * @return String
     * @Title: getInputString
     * @date 2012-12-14 上午9:14:19
     */
    private String getFilePath(Uri mUri) {
        try {
            if (mUri.getScheme().equals("file")) {
                return mUri.getPath();
            } else {
                return getFilePathByUri(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    /**
     * 此处写方法描述
     *
     * @param mUri
     * @return String
     * @Title: getFilePathByUri
     * @date 2012-12-14 上午9:16:33
     */
    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
        String imgPath;
        Cursor cursor = mContentResolver.query(mUri, null, null, null, null);
        cursor.moveToFirst();
        imgPath = cursor.getString(1); // 图片文件路径
        return imgPath;
    }

    /**
     * 此处写方法描述
     *
     * @param isRotate 是否旋转图片
     * @return void
     * @Title: startFaceDetection
     * @date 2012-12-14 上午10:38:29
     */
    private void startFaceDetection(final boolean isRotate) {

        Logger.d(TAG, "startFaceDetection  isRotate:" + isRotate);
        if (isFinishing()) {
            return;
        }
        if (isRotate) {
            initBitmap();
        }

        mImageView.setImageBitmapResetBase(mBitmap, true);

        startBackgroundJob(this, null, getResources().getString(
                R.string.runningFaceDetection), new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);

                mHandler.post(new Runnable() {
                    public void run() {

                        final Bitmap b = mBitmap;
                        if (b != mBitmap && b != null) {

                            mImageView.setImageBitmapResetBase(b, true);
                            mBitmap.recycle();
                            mBitmap = b;
                        }
                        if (mImageView.getScale() == 1F) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mRunFaceDetection.run();
            }
        }, mHandler);
    }

    /**
     * 旋转原图
     *
     * @return void
     * @Title: initBitmap
     * @date 2012-12-13 下午5:37:15
     */
    private void initBitmap() {
        Matrix m = new Matrix();
        m.setRotate(90);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        Logger.d(TAG, "initBitmap  width:" + width);
        Logger.d(TAG, "initBitmap  height:" + height);

        try {
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, m, true);
        } catch (OutOfMemoryError ooe) {

            m.postScale((float) 1 / sampleSize, (float) 1 / sampleSize);
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, m, true);

        }

    }

    private static class BackgroundJob extends
            MonitoredActivity.LifeCycleAdapter implements Runnable {

        private final MonitoredActivity mActivity;
        private final ProgressDialog mDialog;
        private final Runnable mJob;
        private final Handler mHandler;
        private final Runnable mCleanupRunner = new Runnable() {
            public void run() {
                mActivity.removeLifeCycleListener(BackgroundJob.this);
                if (mDialog.getWindow() != null)
                    mDialog.dismiss();
            }
        };

        public BackgroundJob(MonitoredActivity activity, Runnable job,
                             ProgressDialog dialog, Handler handler) {
            mActivity = activity;
            mDialog = dialog;
            mJob = job;
            mActivity.addLifeCycleListener(this);
            mHandler = handler;
        }

        public void run() {
            try {
                mJob.run();
            } finally {
                mHandler.post(mCleanupRunner);
            }
        }

        @Override
        public void onActivityDestroyed(MonitoredActivity activity) {
            // We get here only when the onDestroyed being called before
            // the mCleanupRunner. So, run it now and remove it from the queue
            mCleanupRunner.run();
            mHandler.removeCallbacks(mCleanupRunner);
        }

        @Override
        public void onActivityStopped(MonitoredActivity activity) {
            mDialog.hide();
        }

        @Override
        public void onActivityStarted(MonitoredActivity activity) {
            mDialog.show();
        }
    }

    private static void startBackgroundJob(MonitoredActivity activity,
                                           String title, String message, Runnable job, Handler handler) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        ProgressDialog dialog = ProgressDialog.show(activity, title, message,
                true, false);
        new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
    }

    Runnable mRunFaceDetection = new Runnable() {
        float mScale = 1F;
        Matrix mImageMatrix;

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            //mImageView.re
            if (hv != null) {
                mImageView.remove(hv);
            }
            hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // make the default size about 4/5 of the width or height
            //shark
            //            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropWidth = Math.min(width, height);
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {
                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
                    mAspectX != 0 && mAspectY != 0);
            mImageView.add(hv);
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();

            mScale = 1.0F / mScale;
            mHandler.post(new Runnable() {
                public void run() {
                    makeDefault();

                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }
                }
            });
        }
    };


    /**
     * 旋转图片，每次以90度为单位
     *
     * @return void
     * @Title: onRotateClicked
     * @date 2012-12-12 下午5:19:21
     */
    private void onRotateClicked() {

        startFaceDetection(true);

    }

    /**
     * 点击保存的处理，这里保存成功回传的是一个Uri，系统默认传回的是一个bitmap图，
     * 如果传回的bitmap图比较大的话就会引起系统出错。会报这样一个异常：
     * android.os.transactiontoolargeexception。为了规避这个异常，
     * 采取了传回Uri的方法。
     *
     * @return void
     * @Title: onSaveClicked
     * @date 2012-12-14 上午10:32:38
     */
    private void onSaveClicked() {
        // TODO this code needs to change to use the decode/crop/encode single
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mCrop == null) {
            return;
        }

        if (mSaving)
            return;
        mSaving = true;

        final Bitmap croppedImage;

        Rect r = mCrop.getCropRect();

        int width = r.width();
        int height = r.height();
        //
        Logger.d("onSaveClicked", "width:" + width);
        Logger.d("onSaveClicked", "height:" + height);

        // If we are circle cropping, we want alpha channel, which is the
        // third param here.

        croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(croppedImage);
        Rect dstRect = new Rect(0, 0, width, height);

        canvas.drawBitmap(mBitmap, r, dstRect, null);

        // Release bitmap memory as soon as possible
        //        mImageView.clear();
        //        mBitmap.recycle();
        //        mBitmap = null;


        /*mImageView.setImageBitmapResetBase(croppedImage, true);
        mImageView.center(true, true);
        mImageView.mHighlightViews.clear();*/
        
        /*// 缩放图片的尺寸    指定大小300*300
        int ww = croppedImage.getWidth();
		int wh = croppedImage.getHeight();
        float scaleWidth = (float) 300 / ww;  
        float scaleHeight = (float) 300 / wh;  
		Logger.d(TAG, "croppedImage scaleWidth:" + scaleWidth);
		
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);  
        // 产生缩放后的Bitmap对象  
        final Bitmap resizeBitmap = Bitmap.createBitmap(croppedImage, 0, 0, ww, wh, matrix, false);  
		Logger.d(TAG, "resizeBitmap ww:" + resizeBitmap.getWidth());
		Logger.d(TAG, "resizeBitmap wh:" + resizeBitmap.getHeight());*/

        String imgPath = getFilePath(targetUri);
        //截取后的图片路径uri
        final String cropPath = Constant.CACHEPATH + "/" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        Logger.d("onSaveClicked", "cropPath:" + cropPath);

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                saveDrawableToCache(croppedImage, cropPath);
                croppedImage.recycle();
            }

        });

        Uri cropUri = Uri.fromFile(new File(cropPath));
        Logger.d("onSaveClicked", "cropPath:" + cropPath);
        Logger.d("onSaveClicked", "cropUri:" + cropUri);

        //跳转到贴图的界面
        //        Intent intent = new Intent(this, WaterMarkActivity.class);
        Intent intent = new Intent(this, WaterMarkActivity_ImageFilter.class);//跳转到滤镜界面

        intent.putExtra(TakePicActivity.CROP_IMAGE_URI, cropPath);
        startActivityForResult(intent, REQ_CODE);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBitmap.recycle();
        mBitmap = null;
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

