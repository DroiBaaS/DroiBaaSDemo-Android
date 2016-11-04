package com.droi.sdk.droibaasdemo.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.droibaasdemo.R;

/**
 * Created by chenpei on 2016/5/23.
 */

public class RegisterFragment extends Fragment {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private ProgressDialog mProgressView;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        DroiAnalytics.onFragmentStart(getActivity(), "RegisterFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "RegisterFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) view.findViewById(R.id.confirm_password);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) view.findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView toLoginButton = (TextView) view.findViewById(R.id.to_login_fragment);
        toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Fragment loginFragment = new LoginFragment();
                transaction.replace(R.id.droi_login_container, loginFragment);
                transaction.commit();
            }
        });

        mProgressView = new ProgressDialog(getActivity());
        return view;
    }

    private void attemptLogin() {
        //计数事件
        DroiAnalytics.onEvent(getActivity(), "register");

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_not_same));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            DroiUser user = new DroiUser();
            user.setUserId(email);
            user.setPassword(password);
            user.signUpInBackground(new DroiCallback<Boolean>() {
                @Override
                public void result(Boolean aBoolean, DroiError droiError) {
                    showProgress(false);
                    if (droiError.isOk()) {
                        activity.finish();
                    } else {
                        if (droiError.getCode() == DroiError.USER_ALREADY_EXISTS) {
                            mPasswordView.setError(getString(R.string.error_user_already_exists));
                            mPasswordView.requestFocus();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT);
                        }
                    }
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isConfirmPasswordValid(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    /**
     * Shows the progress
     */
    private void showProgress(final boolean show) {
        if (show) {
            mProgressView.show();
        } else {
            mProgressView.dismiss();
        }
    }
}
