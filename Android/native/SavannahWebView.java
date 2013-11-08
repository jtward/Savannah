package uk.co.tealspoon.savannahechoexample;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class SavannahWebView extends WebView {

	public SavannahWebView(Context context) {
		super(context);
	}
	
	public SavannahWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SavannahWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
	}
	
	@Override
	public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
		super.loadUrl(url, additionalHttpHeaders);
	}
}
