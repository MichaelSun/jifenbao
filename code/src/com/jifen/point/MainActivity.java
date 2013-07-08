package com.jifen.point;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jifen.api.LoginRequest;
import com.jifen.api.LoginResponse;
import com.jifen.api.RegisteRequest;
import com.jifen.api.RegisteResponse;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.common.utils.Environment;
import com.plugin.common.utils.UtilsConfig;
import com.plugin.internet.InternetUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity {

    private EditText mUserNameEditText;
    private EditText mPasswordEditText;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserNameEditText = (EditText) findViewById(R.id.username);
        mPasswordEditText = (EditText) findViewById(R.id.password);
        SettingManager.getInstance().init(getApplicationContext());
        UtilsConfig.init(getApplicationContext());

        if (!TextUtils.isEmpty(SettingManager.getInstance().getUserName())) {
            mUserNameEditText.setText(SettingManager.getInstance().getUserName());
        }
        if (!TextUtils.isEmpty(SettingManager.getInstance().getPassword())) {
            mPasswordEditText.setText(SettingManager.getInstance().getPassword());
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.logining));

        View registe = findViewById(R.id.registe);
        registe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                registe();
            }
        });

        View login = findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String userName = mUserNameEditText.getEditableText().toString();
                final String password = mPasswordEditText.getEditableText().toString();
                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                    mProgressDialog.setMessage(getString(R.string.login));
                    mProgressDialog.show();

                    CustomThreadPool.asyncWork(new Runnable() {

                        @Override
                        public void run() {
                            LoginRequest request = new LoginRequest(userName, password);
                            try {
                                LoginResponse response = InternetUtils.request(getApplicationContext(), request);
                                if (response != null) {
                                    Log.d(">>>>>>>", response.toString());

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            mProgressDialog.dismiss();
                                        }
                                    });

                                    switch (response.code) {
                                    case LoginResponse.CODE_SUCCESS:
                                        final String point = response.data;
                                        SettingManager.getInstance().setUserName(userName);
                                        SettingManager.getInstance().setPassword(password);
                                        MobclickAgent.onEvent(getApplicationContext(), "login");
                                        MobclickAgent.flush(getApplicationContext());
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Intent i = new Intent();
                                                i.setClass(getApplicationContext(), ContentActivity.class);
                                                i.putExtra("point", point);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                                        break;
                                    case LoginResponse.CODE_USER_NOT_EXIST:
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), R.string.user_not_exist,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        break;
                                    case LoginResponse.CODE_PASSWORD_ERROR:
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), R.string.password_error,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.user_or_password_empty, Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
        });
        UmengUpdateAgent.setUpdateOnlyWifi(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        UmengUpdateAgent.update(this);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_actionbar, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
        case R.id.about:
            showAboutDialog();
            break;
        }
        return true;
    }

    private void showAboutDialog() {
        String version = Environment.getVersionName(getApplicationContext());
        String versionStr = String.format(getString(R.string.version_info), version);
        View v = this.getLayoutInflater().inflate(R.layout.about_view, null);
        TextView versionTV = (TextView) v.findViewById(R.id.version);
        versionTV.setText(versionStr);

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.about).setView(v)
                .setPositiveButton(R.string.confirm, null).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void registe() {
        final String userName = mUserNameEditText.getEditableText().toString();
        final String password = mPasswordEditText.getEditableText().toString();
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            mProgressDialog.setMessage(getString(R.string.registe_tips));
            mProgressDialog.show();

            CustomThreadPool.asyncWork(new Runnable() {

                @Override
                public void run() {
                    RegisteRequest request = new RegisteRequest(userName, password);
                    try {
                        RegisteResponse response = InternetUtils.request(getApplicationContext(), request);
                        if (response != null) {
                            Log.d(">>>>>>>", response.toString());

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mProgressDialog.dismiss();
                                }
                            });

                            switch (response.code) {
                            case LoginResponse.CODE_SUCCESS:
                                // final String point = response.data;
                                SettingManager.getInstance().setUserName(userName);
                                SettingManager.getInstance().setPassword(password);
                                MobclickAgent.onEvent(getApplicationContext(), "registe");
                                MobclickAgent.flush(getApplicationContext());
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent i = new Intent();
                                        i.setClass(getApplicationContext(), ContentActivity.class);
                                        i.putExtra("point", 0);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                                break;
                            case LoginResponse.CODE_USER_NOT_EXIST:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), R.string.user_not_exist,
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            case LoginResponse.CODE_PASSWORD_ERROR:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), R.string.password_error,
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            case LoginResponse.CODE_USER_EXIST:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), R.string.user_exist, Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.user_or_password_empty, Toast.LENGTH_LONG).show();
                }
            });
        }

        initActionbar();
    }

    private void initActionbar() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(R.string.app_name);
        mActionBar.setIcon(R.drawable.icon);
    }

}
