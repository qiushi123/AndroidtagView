package com.pmmq.pmmqproject.ui.tag;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.example.jimageeditdemo.R;
import com.pmmq.pmmqproject.config.Constant;
import com.pmmq.pmmqproject.ui.picture.TakePicActivity;
import com.pmmq.pmmqproject.ui.tag.TagView.TagViewListener;
import com.pmmq.pmmqproject.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowTagActivity extends Activity implements OnClickListener, TagViewListener {
    private String TAG = "ShowTagActivity";

    private static int IMAGEDISPLAYWIDTH = 300;        //此处是image的显示宽高，标签的坐标是在图片中的比例

    private Uri mImageUri;            //目标图片的Uri
    private String mImagePath;        //目标图片的路径

    private ImageView mImageView;
    private RelativeLayout mImageRootLayout;

    private List<TagView> tagViews = new ArrayList<TagView>();
    private List<TagInfo> tagInfoList = new ArrayList<TagInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tag);

        mImagePath = getIntent().getStringExtra(TakePicActivity.CROP_IMAGE_URI);
        mImageUri = Uri.parse(mImagePath);
        Logger.d(TAG, "imagePath:" + mImagePath);
        Logger.d(TAG, "mImageUri:" + mImageUri);

        String tagInfoListStr = getIntent().getStringExtra("tagInfoList");
        Logger.w(TAG, "tagInfoListStr.toString():" + tagInfoListStr.toString());
        parseInfoData(tagInfoListStr);

        initView();

        addTagView();
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.st_image);
        mImageView.setOnClickListener(this);
        mImageRootLayout = (RelativeLayout) findViewById(R.id.st_image_layout);

        if (mImageUri != null) {
            mImageView.setImageURI(mImageUri);
        }

    }

    private void addTagView() {

        for (TagInfo tagInfo : tagInfoList) {
            TagView tagView = null;

            //标签的坐标pic_x、pic_y是在图片中的比例，所以要根据显示的图片大小计算出准确位置
            double pointX = tagInfo.pic_x * IMAGEDISPLAYWIDTH * Constant.scale;
            double pointY = tagInfo.pic_y * IMAGEDISPLAYWIDTH * Constant.scale;

            switch (tagInfo.direct) {
                case Left:
                    tagView = new TagViewLeft(this, null);

                    tagInfo.leftMargin = (int) (pointX - 15 * Constant.scale);    //根据屏幕密度计算使动画中心在点击点，15dp是margin
                    tagInfo.topMargin = (int) (pointY - 15 * Constant.scale);
                    tagInfo.rightMargin = 0;
                    tagInfo.bottomMargin = 0;
                    break;
                case Right:
                    tagView = new TagViewRight(this, null);

                    tagInfo.leftMargin = 0;
                    tagInfo.topMargin = (int) (pointY - 15 * Constant.scale);
                    tagInfo.rightMargin = (int) (IMAGEDISPLAYWIDTH * Constant.scale) - (int) pointX - (int) (15 * Constant.scale);
                    tagInfo.bottomMargin = 0;
                    break;
            }

            tagView.setData(tagInfo);
            tagView.setTagViewListener(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(tagInfo.leftMargin, tagInfo.topMargin, tagInfo.rightMargin, tagInfo.bottomMargin);
            mImageRootLayout.addView(tagView, params);
            tagViews.add(tagView);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.st_image:
                showOrHideTagView();
                break;

            default:
                break;
        }
    }

    /**
     * 显示隐藏标签
     */
    private void showOrHideTagView() {
        for (TagView tagView : tagViews) {
            if (tagView.isShow) {
                tagView.setVisibility(View.GONE);
            } else {
                tagView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void parseInfoData(String infoStr) {

        try {
            JSONArray jsonArray = new JSONArray(infoStr);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject infoObject = jsonArray.getJSONObject(i);
                TagInfo tagInfo = new TagInfo().getInstance(infoObject);
                tagInfoList.add(tagInfo);
            }
            Logger.w(TAG, "tagInfoList.toString():" + tagInfoList.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onTagViewClicked(View view, TagInfo info) {
        // TODO Auto-generated method stub
        Toast.makeText(this, info.bname, Toast.LENGTH_SHORT).show();
    }
}
