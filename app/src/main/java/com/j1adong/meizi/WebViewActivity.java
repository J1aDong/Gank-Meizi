package com.j1adong.meizi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.j1adong.meizi.activity.BaseActivity;
import com.j1adong.meizi.util.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by J1aDong on 16/7/22.
 */
public class WebViewActivity extends BaseActivity {

    @BindView(R.id.wv_content)
    WebView mWvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        StatusBarUtil.setTranslucent(this, 50);

        String url = getIntent().getStringExtra(Config.URL);
        mWvContent.loadUrl(url);

        mWvContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public void onPreActivityAnimation() {

    }
}
