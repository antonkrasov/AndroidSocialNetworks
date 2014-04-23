package com.androidsocialnetworks.lib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OAuthActivity extends Activity {

    public static final String PARAM_URL_TO_LOAD = "OAuthActivity.PARAM_URL_TO_LOAD";
    public static final String PARAM_CALLBACK = "OAuthActivity.PARAM_CALLBACK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asn_activity_oauth);

        final String paramUrlToLoad = getIntent().getStringExtra(PARAM_URL_TO_LOAD);
        final String paramCallback = getIntent().getStringExtra(PARAM_CALLBACK);

        if (TextUtils.isEmpty(paramUrlToLoad)) {
            throw new IllegalArgumentException("required PARAM_URL_TO_LOAD");
        }

        if (TextUtils.isEmpty(paramCallback)) {
            throw new IllegalArgumentException("required PARAM_CALLBACK");
        }

        WebView webView = (WebView) findViewById(R.id.web_view);
        final View progressContainer = findViewById(R.id.progress_container);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressContainer.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(paramCallback)) {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(url));
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                Intent intent = new Intent();
                intent.setAction(description);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        webView.loadUrl(paramUrlToLoad);
    }

}
