package com.hc.twer;


import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.DoubleBounce;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelsThirdFragment extends Fragment implements View.OnClickListener{

    private ProgressBar progressBar;
    private WebView webView;
    private WebSettings webSettings;

    private AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.8F);
    private ImageView lastPage;
    private ImageView refresh;
    private ImageView search;
    private TravelsSearchDialogFragment travelsSearchDialogFragment = new TravelsSearchDialogFragment();

    private Bundle bundle;
    private String webAddress;
    private String title;

    public TravelsThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travels_third, container, false);
        webView = view.findViewById(R.id.webview);
        progressBar = view.findViewById(R.id.spin_kit);
        lastPage = view.findViewById(R.id.action_last_page);
        refresh = view.findViewById(R.id.action_refresh);
        search = view.findViewById(R.id.action_websearch);

        init();
        setWebView();

        return view;
    }

    private void init()
    {
        ((MainActivity)getActivity()).setTbackButtonEnabled(true);
        lastPage.setOnClickListener(this);
        refresh.setOnClickListener(this);
        search.setOnClickListener(this);
    }

    private void setWebView()
    {
        bundle = getArguments();
        title = bundle.getString("title");
        webAddress = bundle.getString("webAddress");

        // change toolbar's title
        TextView toolbar_title = getActivity().findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);

        // webPage loading
        webViewLoading();

        // webView's Attributions
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.canGoBack();
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.loadUrl(webAddress);
    }

    private void webViewLoading()
    {
        // webPage loading
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setIndeterminateDrawable(new DoubleBounce());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(alphaAnimation);
        switch (v.getId())
        {
            case R.id.action_last_page:
                webView.goBack();
                webViewLoading();
                break;
            case R.id.action_refresh:
                webView.reload();
                webViewLoading();
                break;
            case R.id.action_websearch:
                travelsSearchDialogFragment.show(getFragmentManager(), "travelsSearchDialog");
                break;
        }
    }
}
