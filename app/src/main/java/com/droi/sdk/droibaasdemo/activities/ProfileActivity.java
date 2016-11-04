package com.droi.sdk.droibaasdemo.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.models.MyUser;
import com.droi.sdk.droibaasdemo.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by chenpei on 2016/5/30.
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "ProfileActivity";
    TextView userNameText;
    ImageView headImageView;
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;
    private ProgressBar progressBar;
    View selectPic;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = this;
        initUI();
        refreshView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshView() {
        MyUser user = DroiUser.getCurrentUser(MyUser.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            userNameText.setText(user.getUserId());
            if (user.getHeadIcon() != null) {
                user.getHeadIcon().getUriInBackground(new DroiCallback<Uri>() {
                    @Override
                    public void result(Uri uri, DroiError droiError) {
                        if (droiError.isOk()){
                            Picasso.with(mContext).load(uri).into(headImageView);
                        }
                    }
                });
            }
        } else {
            headImageView.setImageResource(R.drawable.profile_default_icon);
            userNameText.setText(R.string.fragment_mine_login);
        }
    }

    private void initUI() {
        TextView title = (TextView) findViewById(R.id.top_bar_title);
        title.setText(getString(R.string.profile));
        userNameText = (TextView) findViewById(R.id.user_id);
        headImageView = (ImageView) findViewById(R.id.head_pic);
        findViewById(R.id.head).setOnClickListener(this);
        findViewById(R.id.profile_logout).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        ImageButton backArrowButton = (ImageButton) findViewById(R.id.top_bar_back_btn);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
        btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) this.findViewById(R.id.progress_bar);
        selectPic = findViewById(R.id.select_pic);
        layout = (LinearLayout) findViewById(R.id.pop_layout);
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        DroiUser user = DroiUser.getCurrentUser();
        switch (v.getId()) {
            case R.id.head:
                selectPic.setVisibility(View.VISIBLE);
                break;
            case R.id.profile_logout:
                DroiAnalytics.onEvent(this, "logout");
                DroiError droiError;
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    droiError = user.logout();
                    if (droiError.isOk()) {
                        Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.logout_failed, Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
                break;
            case R.id.top_bar_back_btn:
                finish();
                break;
            case R.id.change_password:
                Intent changePasswordIntent = new Intent(this, ChangePasswordActivity.class);
                startActivity(changePasswordIntent);
                break;
            case R.id.btn_take_photo:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_pick_photo:
                try {
                    Intent intent;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                    } else {
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                    }
                    intent.setType("image/*");
                    startActivityForResult(intent, 2);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancel:
                selectPic.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        if (resultCode != RESULT_OK) {
            Log.i(TAG, "resultCode != RESULT_OK");
            Toast.makeText(mContext, "获取图片失败", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        if (data != null) {
            upload(data);
        } else {
            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
            selectPic.setVisibility(View.GONE);
        }
    }

    private void upload(Intent data) {
        Log.i(TAG, "upload");
        Uri mImageCaptureUri = data.getData();
        if (mImageCaptureUri != null) {
            Log.i(TAG, "pick pic");
            Bitmap image;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                if (image != null) {
                    String path = CommonUtils.getPath(this, mImageCaptureUri);
                    Log.i("TEST", "path=" + path);
                    DroiFile headIcon = new DroiFile(new File(path));
                    MyUser user = DroiUser.getCurrentUser(MyUser.class);
                    user.setHeadIcon(headIcon);
                    user.saveInBackground(new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            if (aBoolean) {
                                Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                            selectPic.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                    selectPic.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                selectPic.setVisibility(View.GONE);
            }
        } else {
            Log.i(TAG, "take photo");
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                Bitmap image = extras.getParcelable("data");
                if (image != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    DroiFile headIcon = new DroiFile(imageBytes);
                    MyUser user = DroiUser.getCurrentUser(MyUser.class);
                    user.setHeadIcon(headIcon);
                    user.saveInBackground(new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            if (aBoolean) {
                                Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                            selectPic.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                    selectPic.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                selectPic.setVisibility(View.GONE);
            }
        }
    }
}
