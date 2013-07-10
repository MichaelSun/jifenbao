package com.jifen.point;

import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jifen.util.Utils;
import com.jifen.util.Utils.PointFetchListener;
import com.plugin.common.utils.Environment;
import com.umeng.analytics.MobclickAgent;

public class ContentActivity extends BaseActivity implements PointsChangeNotify {

    private int mCurrentPoint = 0;

    private TextView mTipsTextView;

    public static final String CURRENT_POINT_CHANGED = "com.jifen.bao.point";

    private ProgressDialog mProgressDialog;

    private BroadcastReceiver mPointBCR = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CURRENT_POINT_CHANGED)) {
                int point = intent.getIntExtra("point", -1);
                Config.LOGD("[[ContentActivity::BroadcastReceiver]] receive point : " + point);
                uploadPointText(point);
            }
        }
    };

    private static final int REFRESH_POINT = 0;
    private static final int DISMISS_DIALOG = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH_POINT:
                uploadPointText(msg.arg1);
                mProgressDialog.dismiss();
                break;
            case DISMISS_DIALOG:
                mProgressDialog.dismiss();
                break;
            default:
                break;
            }
        }
    };

    private void uploadPointText(int point) {
        if (point != -1) {
            mCurrentPoint = point;
            runOnUiThread(new Runnable() {
                public void run() {
                    mTipsTextView.setText(String.format(getString(R.string.current_point), mCurrentPoint));
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_content);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        initActionbar();

        if (getIntent() == null) {
            mCurrentPoint = 0;
        } else {
            String pointString = getIntent().getStringExtra("point");
            if (!TextUtils.isEmpty(pointString)) {
                mCurrentPoint = Integer.valueOf(pointString);
            } else {
                mCurrentPoint = 0;
            }
        }

        mTipsTextView = (TextView) findViewById(R.id.point);
        mTipsTextView.setText(String.format(getString(R.string.current_point), mCurrentPoint));

        View pointView = findViewById(R.id.point_btn);
        pointView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getApplicationContext(), "show_wall");
                MobclickAgent.flush(getApplicationContext());
                OffersManager.getInstance(ContentActivity.this).showOffersWall();
            }
        });

        // PointsManager.getInstance(this).registerNotify(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CURRENT_POINT_CHANGED);
        registerReceiver(mPointBCR, filter);

        Utils.sendLoginOrRegisteBroadcast(this.getApplicationContext(), SettingManager.getInstance().getUserName(),
                SettingManager.getInstance().getPassword());
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.detail_actionbar, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
        case R.id.about:
            showAboutDialog();
            break;
        case R.id.action_load:
            refreshCurrentPoint();
            break;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // PointsManager.getInstance(this).unRegisterNotify(this);
        unregisterReceiver(mPointBCR);
        // OffersManager.getInstance(this).onAppExit();
    }

    @Override
    public void onPointBalanceChange(int pointsBalance) {
        // mTipsTextView.setText(String.format(getString(R.string.current_point),
        // pointsBalance));
    }

    private void refreshCurrentPoint() {
        mProgressDialog.show();
        Utils.asyncFetchCurrentPoint(getApplicationContext(), SettingManager.getInstance().getUserName(),
                SettingManager.getInstance().getPassword(), new PointFetchListener() {
                    @Override
                    public void onPointFetchSuccess(int current) {
                        MobclickAgent.onEvent(getApplicationContext(), "refresh_point");
                        MobclickAgent.flush(getApplicationContext());

                        Message msg = Message.obtain();
                        msg.what = REFRESH_POINT;
                        msg.arg1 = current;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onPointFetchFailed(int code, String data) {
                        mHandler.sendEmptyMessage(DISMISS_DIALOG);
                    }
                });
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

    private void initActionbar() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(R.string.app_name);
        mActionBar.setIcon(R.drawable.icon);
    }
}
