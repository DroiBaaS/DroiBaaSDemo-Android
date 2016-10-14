package com.droi.sdk.droibaasdemo.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.droibaasdemo.R;

/**
 * Created by chenpei on 2016/5/30.
 */
public class ChangePasswordActivity extends BaseActivity {
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText retypeNewPasswordEditText;
    private Button changePasswordButton;
    private ImageButton backArrowButton;
    private Toast mToast = null;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext = this;
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI() {
        TextView title = (TextView) findViewById(R.id.top_bar_title);
        title.setText(getString(R.string.change_password));
        oldPasswordEditText = (EditText) findViewById(R.id.old_password);
        newPasswordEditText = (EditText) findViewById(R.id.new_password);
        retypeNewPasswordEditText = (EditText) findViewById(R.id.retype_new_password);
        backArrowButton = (ImageButton) findViewById(R.id.top_bar_back_btn);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        changePasswordButton = (Button) findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });
    }

    private void attemptChangePassword() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String retypeNewPassword = retypeNewPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(oldPassword) || !isPasswordValid(oldPassword)) {
            showToastInUiThread(getString(R.string.error_invalid_old_password));
            return;
        }
        else if (TextUtils.isEmpty(newPassword) || !isPasswordValid(newPassword)) {
            showToastInUiThread(getString(R.string.error_invalid_password));
            return;
        }
        else if (!isConfirmPasswordValid(newPassword, retypeNewPassword)) {
            showToastInUiThread(getString(R.string.error_password_not_same));
            return;
        }
        DroiUser myUser = DroiUser.getCurrentUser();
        if (myUser != null && myUser.isAuthorized() && !myUser.isAnonymous()) {
            myUser.changePasswordInBackground(oldPassword, newPassword, new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    if (aBoolean) {
                        showToastInUiThread(getString(R.string.change_password_success));
                        finish();
                    } else {
                        showToastInUiThread(getString(R.string.change_password_failed));
                    }
                }
            });
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isConfirmPasswordValid(String newPassword, String retypeNewPassword) {
        return newPassword.equals(retypeNewPassword);
    }

    private void showToastInUiThread(final String msg) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(mContext.getApplicationContext(),
                            msg, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(msg);
                }
                mToast.show();
            }
        });
    }
}
