package com.pmmq.pmmqproject.ui.tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.jimageeditdemo.R;
import com.pmmq.pmmqproject.config.Constant;
import com.pmmq.pmmqproject.ui.picture.TakePicActivity;
import com.pmmq.pmmqproject.ui.tag.TagInfo.Direction;
import com.pmmq.pmmqproject.ui.tag.TagInfo.Type;
import com.pmmq.pmmqproject.ui.tag.TagView.TagViewListener;
import com.pmmq.pmmqproject.util.Logger;
import com.pmmq.pmmqproject.util.NormalUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author shark
 * @ClassName: AddTagActivity
 * @Description: 添加标签，仿nice
 * @date 2014年11月26日 上午10:57:47
 */
public class AddTagActivity extends Activity implements OnClickListener, TagViewListener {

    private String TAG = "AddTagActivity";
    private final static int TEXTSIZE = 12;        //Tagview的字体大小

    private RelativeLayout mImageRootLayout;
    private LinearLayout mTagLinearLayout;
    private ImageView mImageView;
    private ImageView mTagNormal;
    private ImageView mTagGeo;
    private ImageView mTagUser;
    private Button mShowBtn;
    private Button mFinishBtn;

    private Uri mImageUri;            //目标图片的Uri
    private String mImagePath;        //目标图片的路径

    private Boolean isTagLayShow = false;    //标签图标区域是否显示

    private float mPointX = 0;
    private float mPointY = 0;
    private float x1;
    private float y1;

    private List<TagView> tagViews = new ArrayList<TagView>();
    private List<TagInfo> tagInfoList = new ArrayList<TagInfo>();

    private final Handler mHandler = new Handler();

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        mImagePath = getIntent().getStringExtra(TakePicActivity.CROP_IMAGE_URI);
        mImageUri = Uri.parse(mImagePath);
        Logger.d(TAG, "imagePath:" + mImagePath);
        Logger.d(TAG, "mImageUri:" + mImageUri);

        initView();

    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.at_image);
        //		mImageView.setOnClickListener(this);
        mImageView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int action = event.getAction();
                //				Logger.w(TAG, "!!!!!!!!!!  action:" + action);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        mPointX = event.getX();
                        mPointY = event.getY();

					/*Logger.w(TAG, "!!!!!!!!!!" + String.valueOf(mPointX));
                    Logger.w(TAG, String.valueOf(mPointY)); */
                        showTagLinearLayout(mPointX, mPointY);
                        break;
                }

                return true;
            }
        });

        mImageRootLayout = (RelativeLayout) findViewById(R.id.at_image_layout);

        LayoutParams params = new LayoutParams(Constant.displayWidth, Constant.displayWidth);
        mImageView.setLayoutParams(params);
        if (mImageUri != null) {
            mImageView.setImageURI(mImageUri);
        }

        mTagLinearLayout = (LinearLayout) findViewById(R.id.at_tag_layout);
        LayoutParams params2 = new LayoutParams(Constant.displayWidth, Constant.displayWidth);
        mTagLinearLayout.setLayoutParams(params2);

        mTagNormal = (ImageView) findViewById(R.id.at_tag_image1);
        mTagGeo = (ImageView) findViewById(R.id.at_tag_image2);
        mTagUser = (ImageView) findViewById(R.id.at_tag_image3);
        mTagNormal.setOnClickListener(this);
        mTagGeo.setOnClickListener(this);
        mTagUser.setOnClickListener(this);

        mShowBtn = (Button) findViewById(R.id.at_show);
        mShowBtn.setOnClickListener(this);
        mFinishBtn = (Button) findViewById(R.id.at_finish);
        mFinishBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.at_show:

                JSONArray tagInfoArray = new JSONArray();
                for (TagInfo info : tagInfoList) {
                    JSONObject infoJson = info.getjson();
                    tagInfoArray.put(infoJson);
                }
                Intent intent = new Intent(this, ShowTagActivity.class);
                intent.putExtra(TakePicActivity.CROP_IMAGE_URI, mImagePath);
                intent.putExtra("tagInfoList", tagInfoArray.toString());
                startActivity(intent);

                break;
            case R.id.at_finish:
                saveImageAndFinish();

                break;
            case R.id.at_tag_image1:
                editTagInfo(1);
                break;
            case R.id.at_tag_image2:
                editTagInfo(2);
                break;
            case R.id.at_tag_image3:
                editTagInfo(3);
                break;

            default:
                break;
        }
    }

    private void saveImageAndFinish() {
        final Bitmap croppedImage = getLayoutBitmap(mImageRootLayout);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                saveDrawableToCache(croppedImage, mImagePath);
                croppedImage.recycle();
            }
        });

        Intent intent = new Intent();
        intent.putExtra("tag_image_path", mImagePath);
        setResult(RESULT_OK, intent);
        finish();

    }

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

    //shark_5 获取Layout的截图
    public Bitmap getLayoutBitmap(View view) {
        view.invalidate();
        //shark_5  
        view.setDrawingCacheEnabled(true);
        view.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight());

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 点击图片后添加小圆点
     */
    private void addPoint(float x, float y) {
        //		Bitmap photo = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        Bitmap photo = BitmapFactory.decodeFile(mImagePath);
        Bitmap point = BitmapFactory.decodeResource(this.getResources(), R.drawable.brand_tag_point_white_bg);
        Bitmap a = createBitmap(photo, point, x, y);
        mImageView.setImageBitmap(a);
        photo.recycle();
        point.recycle();
        //		a.recycle();
        //		saveMyBitmap(a);
    }

    /**
     * 点击图片后添加小圆点
     */
    private Bitmap createBitmap(Bitmap src, Bitmap point, float x, float y) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        Logger.w(TAG, "createBitmap w:" + w);
        Logger.w(TAG, "createBitmap h:" + h);
        int ww = point.getWidth();
        int wh = point.getHeight();
        Logger.w(TAG, "watermark ww:" + ww);
        Logger.w(TAG, "watermark wh:" + wh);

        //create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        //创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        //draw src into
        cv.drawBitmap(src, 0, 0, null);//在 0，0坐标开始画入src
        //draw watermark into
        cv.drawBitmap(point, (x - (ww / 2)), (y - (wh / 2)), null);//在src的右下角画入水印
        //save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        //store
        cv.restore();//存储

        point.recycle();
        return newb;
    }

    /**
     * 编辑标签信息
     */
    private void editTagInfo(int k) {
        //标签图标区域消失
        showTagLinearLayout(0, 0);
        //		Toast.makeText(this, k + "", Toast.LENGTH_SHORT).show();

        TagInfo tagInfo = new TagInfo();
        tagInfo.bid = 2L;
        tagInfo.bname = "Hello PMMQ " + k;
        tagInfo.direct = getDirection(tagInfo.bname);
        tagInfo.pic_x = mPointX / Constant.displayWidth;
        tagInfo.pic_y = mPointY / Constant.displayWidth;
        tagInfo.type = getRandomType();
        switch (tagInfo.direct) {
            case Left:
                tagInfo.leftMargin = (int) (mPointX - 15 * Constant.scale);    //根据屏幕密度计算使动画中心在点击点，15dp是margin
                tagInfo.topMargin = (int) (mPointY - 15 * Constant.scale);
                tagInfo.rightMargin = 0;
                tagInfo.bottomMargin = 0;
                break;
            case Right:
                tagInfo.leftMargin = 0;
                tagInfo.topMargin = (int) (mPointY - 15 * Constant.scale);
                tagInfo.rightMargin = Constant.displayWidth - (int) mPointX - (int) (15 * Constant.scale);
                tagInfo.bottomMargin = 0;
                break;
        }
        Logger.w(TAG, "11 tagInfo.pic_x:" + tagInfo.pic_x);
        Logger.w(TAG, "tagInfo.pic_y:" + tagInfo.pic_y);
        addTagInfo(tagInfo);

        Logger.w(TAG, "-@@@@------- tagInfo.leftMargin:" + tagInfo.leftMargin);
        Logger.w(TAG, "tagInfo.topMargin:" + tagInfo.topMargin);
        Logger.w(TAG, "tagInfo.rightMargin:" + tagInfo.rightMargin);
        Logger.w(TAG, "tagInfo.bottomMargin:" + tagInfo.bottomMargin);
    }

    /**
     * 添加标签
     */
    private void addTagInfo(final TagInfo tagInfo) {
        TagView tagView = null;
        switch (tagInfo.direct) {
            case Left:
                tagView = new TagViewLeft(this, null);
                break;
            case Right:
                tagView = new TagViewRight(this, null);
                break;
        }
        tagView.setData(tagInfo);
        tagView.setTagViewListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(tagInfo.leftMargin, tagInfo.topMargin, tagInfo.rightMargin, tagInfo.bottomMargin);
        mImageRootLayout.addView(tagView, params);
        //添加TagView的移动事件
        tagView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                        xInView = event.getX();
                        yInView = event.getY();
                        xDownInScreen = event.getRawX();
                        yDownInScreen = event.getRawY() - getStatusBarHeight();
                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY() - getStatusBarHeight();
                        Logger.w(TAG, "statusBarHeight:" + statusBarHeight);

                        x1 = event.getRawX();
                        y1 = event.getRawY() - getStatusBarHeight();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY() - getStatusBarHeight();
                        //手指超出边界时，使y值等于边界值，由于此处view等于屏幕宽度，所以不考虑x值
                        if ((yInScreen - yInView) < 0) {
                            yInScreen = yInView;
                            Logger.w(TAG, "yInScreen:" + yInScreen);
                        } else if ((yInScreen + v.getHeight() - yInView) > Constant.displayWidth) {
                            yInScreen = Constant.displayWidth - v.getHeight() + yInView;
                            Logger.w(TAG, "yInScreen:" + yInScreen);
                        }

                        updateTagViewPosition(v, tagInfo);

                        xDownInScreen = xInScreen;
                        yDownInScreen = yInScreen;

                        break;
                    case MotionEvent.ACTION_UP:
                        Logger.w(TAG, "图片各个角Left：" + v.getLeft() + "-Right：" + v.getRight() + "-Top：" + v.getTop() + "-Bottom：" + v.getBottom());

                        switch (tagInfo.direct) {
                            case Left:
                                tagInfo.pic_x = (v.getLeft() + 15 * Constant.scale) / Constant.displayWidth;
                                tagInfo.pic_y = (v.getTop() + 15 * Constant.scale) / Constant.displayWidth;
                                break;
                            case Right:
                                tagInfo.pic_x = (v.getRight() - 15 * Constant.scale) / Constant.displayWidth;
                                tagInfo.pic_y = (v.getTop() + 15 * Constant.scale) / Constant.displayWidth;
                                break;
                        }

                        Logger.w(TAG, "22 tagInfo.pic_x:" + tagInfo.pic_x);
                        Logger.w(TAG, "tagInfo.pic_y:" + tagInfo.pic_y);
                        if (Math.abs(x1 - xInScreen) < 5 && Math.abs(y1 - yInScreen) < 5) {
                            return false;
                        } else {
                            return true;
                        }

                        //					break;
                }

                return false;
            }
        });
        tagInfoList.add(tagInfo);
        tagViews.add(tagView);
    }

    /**
     * 移动TagView，更新位置
     */
    private void updateTagViewPosition(View v, TagInfo tagInfo) {
        //计算位移
        float xMove = xInScreen - xDownInScreen;
        float yMove = yInScreen - yDownInScreen;

        //获取View的宽度，为什么不用v.getWidth();?因为Right方向的布局有问题，是从屏幕左边缘开始，不符合需求
        int viewWidth = (int) getTagViewWidth(tagInfo.bname);

        switch (tagInfo.direct) {
            case Left:
                tagInfo.leftMargin += xMove;
                tagInfo.topMargin += yMove;
                //边界计算
                if (tagInfo.leftMargin < 0) {
                    tagInfo.leftMargin = 0;
                } else if ((tagInfo.leftMargin + viewWidth) > Constant.displayWidth) {
                    tagInfo.leftMargin = Constant.displayWidth - viewWidth;
                }
                break;
            case Right:
                tagInfo.topMargin += yMove;
                tagInfo.rightMargin -= xMove;
                Logger.w(TAG, "1111111 tagInfo.rightMargin:" + tagInfo.rightMargin);
                //边界计算
                if (tagInfo.rightMargin < 0) {
                    tagInfo.rightMargin = 0;
                    Logger.w(TAG, "222222222 tagInfo.rightMargin:" + tagInfo.rightMargin);
                } else if ((tagInfo.rightMargin + viewWidth) > Constant.displayWidth) {
                    tagInfo.rightMargin = Constant.displayWidth - viewWidth;
                    Logger.w(TAG, "333333333 tagInfo.rightMargin:" + tagInfo.rightMargin);
                }
                break;
        }
        //边界计算
        if (tagInfo.topMargin < 0) {
            tagInfo.topMargin = 0;
        } else if ((tagInfo.topMargin + v.getHeight()) > Constant.displayWidth) {
            tagInfo.topMargin = Constant.displayWidth - v.getHeight();
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(tagInfo.leftMargin, tagInfo.topMargin, tagInfo.rightMargin, tagInfo.bottomMargin);
        v.setLayoutParams(params);
    }

    /*
     * TagView的点击事件
     * (non-Javadoc)
     * @see com.pmmq.pmmqproject.ui.tag.TagView.TagViewListener#onTagViewClicked(android.view.View, com.pmmq.pmmqproject.ui.tag.TagInfo)
     */
    @Override
    public void onTagViewClicked(final View view, final TagInfo info) {
        Logger.w(TAG, "onTagViewClicked");
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.at_want_delete)).setPositiveButton(getResources().getString(R.string.at_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // TODO Auto-generated method stub
                        mImageRootLayout.removeView(view);
                        tagViews.remove(view);
                        tagInfoList.remove(info);
                        Logger.w(TAG, "----> tagViews.size():" + tagViews.size());
                        Logger.w(TAG, "tagInfoList.size():" + tagInfoList.size());
                    }
                }).setNegativeButton(getResources().getString(R.string.at_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO Auto-generated method stub

            }
        }).show();
    }

    /**
     * 获取TagView的宽度
     */
    private float getTagViewWidth(String str) {
        float viewWidth = NormalUtils.GetTextWidth(str, TEXTSIZE * Constant.scale);
        Logger.w(TAG, "getDirection viewSize:" + viewWidth);
        viewWidth += (46 * Constant.scale);            //46dp是TagView除去text部分的宽度，可以从布局中查看
        Logger.w(TAG, "viewWidth:" + viewWidth);
        return viewWidth;
    }

    /**
     * 获取TagView的高度
     */
    private float getTagViewHeight(String str) {
        float viewHeight = (30 * Constant.scale);    //30dp是TagView的高度，可以从布局中查看
        Logger.w(TAG, "viewHeight:" + viewHeight);
        return viewHeight;
    }

    /**
     * 获取TagView的方向
     */
    private Direction getDirection(String str) {
        float showSize = NormalUtils.GetTextWidth(str, TEXTSIZE * Constant.scale);
        showSize += (32 * Constant.scale);
        Logger.w(TAG, "getDirection showSize:" + showSize);
        if ((mPointX + showSize) > Constant.displayWidth) {
            return Direction.Right;
        } else {
            return Direction.Left;
        }
    }

    private Type getRandomType() {
        Random random = new Random();
        int ran = random.nextInt(Type.size());
        if (0 == ran) {
            return Type.Undefined;
        } else if (1 == ran) {
            return Type.Exists;
        } else if (2 == ran) {
            return Type.CustomPoint;
        } else {
            return Type.OfficalPoint;
        }
    }

    /**
     * 显示与隐藏标签图标区域
     */
    private void showTagLinearLayout(float x, float y) {
        if (isTagLayShow) {
            mImageView.setImageURI(mImageUri);
            mTagLinearLayout.setVisibility(View.GONE);
            isTagLayShow = false;
            //设置TagView的可以移动
            setTagViewEnable(true);
        } else {
            addPoint(x, y);
            mTagLinearLayout.setVisibility(View.VISIBLE);
            TranslateAnimation anim = new TranslateAnimation(0, 0, -(Constant.displayWidth / 2), 0);
            anim.setInterpolator(new BounceInterpolator());
            anim.setDuration(1000);
            mTagNormal.setAnimation(anim);
            mTagGeo.setAnimation(anim);
            mTagUser.setAnimation(anim);
            anim.startNow();
            isTagLayShow = true;
            //取消TagView的移动
            setTagViewEnable(false);
        }
    }

    /**
     * 设置TagView是否可以点击
     *
     * @param enabled
     */
    private void setTagViewEnable(Boolean enabled) {
        for (TagView view : tagViews) {
            view.setEnabled(enabled);
        }
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

}
