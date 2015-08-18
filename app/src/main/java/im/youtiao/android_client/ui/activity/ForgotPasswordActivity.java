package im.youtiao.android_client.ui.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import roboguice.activity.RoboActionBarActivity;
import com.umeng.analytics.MobclickAgent;

public class ForgotPasswordActivity extends RoboActionBarActivity {


    WebView forgotPasswordWv;

    YTApplication getApp() {
        return (YTApplication)getApplication();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        forgotPasswordWv =  (WebView)findViewById(R.id.wv_forgot_password);
        forgotPasswordWv.removeJavascriptInterface("searchBoxJavaBredge_");
        forgotPasswordWv.loadUrl(getApp().getYTHost() + "/users/password/new");
        forgotPasswordWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
