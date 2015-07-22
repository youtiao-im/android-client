package im.youtiao.android_client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;
import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;

public class WelcomeActivity extends Activity {

    YTApplication getApp() {
        return (YTApplication)getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

//        ImageView welcomeIv = (ImageView)findViewById(R.id.iv_welcome);
//        welcomeIv.setColorFilter(getResources().getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
        Boolean isUse = getApp().readIsFistUse();

        if (isUse) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getApp().writeIsFirstUse();
                    Intent intent = new Intent(WelcomeActivity.this, BootstrapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 5000);
        } else {
            Intent intent = new Intent(WelcomeActivity.this, BootstrapActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
