package com.droi.sdk.droibaasdemo.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
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

import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.models.MyUser;
import com.droi.sdk.oauth.DroiOauth;
import com.droi.sdk.oauth.OauthError;
import com.droi.sdk.oauth.Scope;
import com.droi.sdk.oauth.callback.DroiAccountLoginCallBack;
import com.droi.sdk.oauth.callback.GetAccountInfoCallBack;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private static String TAG = "LoginFragment";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
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
        DroiAnalytics.onFragmentStart(getActivity(), "LoginFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "LoginFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) view.findViewById(R.id.login_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView toRegister = (TextView) view.findViewById(R.id.to_register_fragment);
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Fragment registerFragment = new RegisterFragment();
                transaction.replace(R.id.droi_login_container, registerFragment);
                transaction.commit();
            }
        });
        mProgressView = new ProgressDialog(getActivity());
        mProgressView.setMessage("Login...");
        final TextView toDroiOauth = (TextView) view.findViewById(R.id.droi_oauth);
        toDroiOauth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oauthLogin();
            }
        });
        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        //计数事件
        DroiAnalytics.onEvent(getActivity(), "login");
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
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

    String mOpenId;
    String mToken;

    private void oauthLogin() {
        Log.i("TEST", "oauthLogin");
        DroiOauth
                .requestTokenAuth(getActivity(), new DroiAccountLoginCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        //登录成功操作
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.has("openid")) {
                                mOpenId = jsonObject.getString("openid");
                            }
                            if (jsonObject.has("token")) {
                                mToken = jsonObject.getString("token");
                            }
                        } catch (JSONException e) {
                            DroiAnalytics.onError(getActivity(), e);
                        }
                        getActivity().finish();
                    }

                    @Override

                    public void onError(String result) {
                        //登录失败操作
                        Log.i(TAG, "error:" + result);
                        DroiAnalytics.onError(getActivity(), result);
                        getActivity().finish();
                    }
                });
    }

    private void getAccountInfo() {
        DroiOauth.getAccountInfo( mOpenId, mToken, new Scope[]{Scope.USERINFO}, new GetAccountInfoCallBack(){
            @Override
            public void onGetAccountInfo(OauthError oauthError, String s) {
                //获取账号信息
            }
        });
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, DroiError> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected DroiError doInBackground(Void... params) {
            DroiError droiError = new DroiError();
            MyUser user = DroiUser.login(mEmail,
                    mPassword, MyUser.class, droiError);
            return droiError;
        }

        @Override
        protected void onPostExecute(final DroiError droiError) {
            mAuthTask = null;
            showProgress(false);
            if (droiError.isOk()) {
                Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();
                activity.finish();
            } else {
                if (droiError.getCode() == DroiError.USER_NOT_EXISTS) {
                    mEmailView.setError(getString(R.string.error_user_not_exists));
                    mEmailView.requestFocus();
                } else if (droiError.getCode() == DroiError.USER_PASSWORD_INCORRECT) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
