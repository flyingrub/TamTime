package flying.grub.tamtime.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gc.materialdesign.views.ProgressBarDeterminate;

import flying.grub.tamtime.R;

public class WebFragment extends Fragment {

    private WebView webView;

    public static WebFragment newInstance(String URL) {
        WebFragment fragmentDemo = new WebFragment();
        Bundle args = new Bundle();
        args.putString("defUrl", URL);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.web_view, container, false);

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        webViewInit();
        getActivity().setTitle(getString(R.string.maps));
        if (savedInstanceState == null) {
            webView.loadUrl(getArguments().getString("defUrl", ""));
        }else{
            webView.restoreState(savedInstanceState);
        }
    }

    public void webViewInit(){
        webView = (WebView) getActivity().findViewById(R.id.webview);
        CookieManager.getInstance().setAcceptCookie(true);
        final ProgressBarDeterminate progressBar = (ProgressBarDeterminate) getActivity().findViewById(R.id.progressDeterminate);

        webView.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

        progressBar.setProgress(0);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Log.d("URL", url);
                //view.loadUrl(url);
                return true;
            }
        });
    }
}