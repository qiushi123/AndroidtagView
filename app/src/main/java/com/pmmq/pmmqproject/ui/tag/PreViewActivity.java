package com.pmmq.pmmqproject.ui.tag;

import com.example.jimageeditdemo.R;
import com.pmmq.pmmqproject.ui.picture.TakePicActivity;
import com.pmmq.pmmqproject.util.Logger;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class PreViewActivity extends Activity{

	private String TAG = "PreViewActivity";
	
	private Uri mImageUri;			//目标图片的Uri
	private String mImagePath;		//目标图片的路径

	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pre_view);
		
		mImagePath = getIntent().getStringExtra("tag_image_path");
		mImageUri = Uri.parse(mImagePath);
		Logger.d(TAG, "imagePath:" + mImagePath);
		Logger.d(TAG, "mImageUri:" + mImageUri);
		
		initView();
		
	}

	private void initView(){
		mImageView = (ImageView)findViewById(R.id.pv_image);
		if(mImageUri != null){
			mImageView.setImageURI(mImageUri);
		}
		
	}
	
	
	
}
