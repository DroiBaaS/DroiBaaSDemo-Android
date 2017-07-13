package com.droi.sdk.droibaasdemo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.models.MyUser;
import com.droi.sdk.droibaasdemo.utils.AvatarUtil;
import com.droi.sdk.droibaasdemo.utils.StringUtil;
import com.droi.sdk.droibaasdemo.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by chenpei on 2016/5/30.
 */

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "ProfileActivity";
    private final int mPhotoPickSize = 96;
    private static final int REQUEST_CODE_CAMERA_WITH_DATA = 1;
    private static final int REQUEST_CODE_PHOTO_PICKED_WITH_DATA = REQUEST_CODE_CAMERA_WITH_DATA + 1;
    private static final int REQUEST_CROP_PHOTO = REQUEST_CODE_PHOTO_PICKED_WITH_DATA + 1;
    private static final int CROP_PHOTO_FROM_GALLERY = 1;
    private static final int CROP_PHOTO_FROM_CAMERA = 2;

    private TextView userNameText;
    private ImageView headImageView;
    private Context mContext;
    private LayoutInflater mInflater;

    //avatar
    private Uri mTempPhotoUri;
    private Uri mCroppedPhotoUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = this;
        mInflater = LayoutInflater.from(getApplicationContext());
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
                        if (droiError.isOk()) {
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
    }

    @Override
    public void onClick(View v) {
        DroiUser user = DroiUser.getCurrentUser();
        switch (v.getId()) {
            case R.id.head:
                showAvatarSourceMenu();
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
            default:
                break;
        }
    }

    private void showAvatarSourceMenu() {

        View view = mInflater.inflate(R.layout.layout_select_avatar_menu, null, false);

        Button fromCameraBtn = (Button) view.findViewById(R.id.btn_take_photo);
        Button fromGalleryBtn = (Button) view.findViewById(R.id.btn_pick_photo);
        Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel);

        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        window.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
        window.setBackgroundDrawable(dw);

        window.setAnimationStyle(R.style.avatar_select_menu_anim_style);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);

        window.showAtLocation(userNameText, Gravity.BOTTOM, 0, 0);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });


        fromCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTakePhoto();
                window.dismiss();
            }
        });

        fromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
                window.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }

    private void chooseTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTempPhotoUri = AvatarUtil.generateTempImageUri(ProfileActivity.this);
        mCroppedPhotoUri = AvatarUtil.generateTempCroppedImageUri(ProfileActivity.this);
        // where to store the image after take photo
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTempPhotoUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA_WITH_DATA);
    }

    private void pickFromGallery() {
        mCroppedPhotoUri = AvatarUtil.generateTempCroppedImageUri(ProfileActivity.this);
        Intent intent = getPhotoPickIntent(null);
        startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKED_WITH_DATA);
    }

    private Intent getPhotoPickIntent(Uri outputUri) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
        }
        return intent;
    }

    private void cropPhoto(Uri uri, Uri outputUri, int source) {
        if (source == CROP_PHOTO_FROM_CAMERA) {
            if (uri != null) {
                File file = new File(uri.getPath());
                if (file == null || file.exists() == false) {
                    return;
                }
            }
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        // which image to crop
        intent.setDataAndType(uri, "image/*");
        // where to save the cropped photo
        AvatarUtil.addPhotoPickerExtras(intent, outputUri);
        // crop data
        AvatarUtil.addCropExtras(intent, mPhotoPickSize);

        try {
            startActivityForResult(intent, REQUEST_CROP_PHOTO);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_WITH_DATA: {
                if (resultCode == Activity.RESULT_OK) {
                    cropPhoto(mTempPhotoUri, mCroppedPhotoUri, CROP_PHOTO_FROM_CAMERA);
                }
            }
            break;

            case REQUEST_CROP_PHOTO: {
                saveImage();
            }
            break;

            case REQUEST_CODE_PHOTO_PICKED_WITH_DATA: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        final Uri toCrop = data.getData();
                        if (toCrop != null) {
                            if (mCroppedPhotoUri == null) {// in case of pick from
                                // gallery, and take
                                // photo
                                mCroppedPhotoUri = AvatarUtil.generateTempCroppedImageUri(ProfileActivity.this);
                            }
                            cropPhoto(toCrop, mCroppedPhotoUri, CROP_PHOTO_FROM_GALLERY);
                        }
                    }
                }
            }
            break;

            default:
                break;
        }
    }

    private void saveImage() {
        if (mCroppedPhotoUri == null) {
            Util.showMessage(getApplicationContext(), R.string.upload_avatar_failed);
            return;
        }

        try {
            Bitmap photo = AvatarUtil.getBitmapFromUri(getApplicationContext(), mCroppedPhotoUri);
            AvatarUtil.saveBitmap(getApplicationContext(), photo);
            uploadAvatar(mCroppedPhotoUri.getPath());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Util.showMessage(getApplicationContext(), R.string.upload_avatar_failed);
        }
    }

    private void uploadAvatar(String filePath) {
        Log.i(TAG, "upload");

        if (StringUtil.isStringNullOrEmpty(filePath)) {
            return;
        }

        File avatarFile = new File(filePath);
        if (!avatarFile.exists() || !avatarFile.isFile()) {
            return;
        }

        final DroiFile headIcon = new DroiFile(avatarFile);
        final MyUser user = DroiUser.getCurrentUser(MyUser.class);
        user.setHeadIcon(headIcon);
        user.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                if (aBoolean) {
                    String url = user.getHeadIcon().getUri().toString();
                    Picasso.with(mContext).load(url).into(headImageView);
                    Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
