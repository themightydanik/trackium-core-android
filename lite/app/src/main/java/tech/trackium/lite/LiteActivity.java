package tech.trackium.lite;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class LiteActivity extends Activity {
    private SharedPreferences profile;
    private LinearLayout shell;
    private WebView browser;
    private boolean clearingSession;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        profile = getSharedPreferences("gateway-profile", MODE_PRIVATE);
        String origin = profile.getString("origin", "");
        if (origin.isEmpty()) showConnection("");
        else open(GatewayOrigin.parse(origin));
    }

    private void frame() {
        shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.VERTICAL);
        shell.setBackgroundColor(Color.WHITE);
        shell.setFitsSystemWindows(true);
        setContentView(shell);
    }

    private void action(String label, Runnable command) {
        Button button = new Button(this);
        button.setText(label);
        button.setOnClickListener(view -> command.run());
        shell.addView(button);
    }

    private void showConnection(String message) {
        closeBrowser();
        frame();
        TextView title = new TextView(this);
        title.setText(message.isEmpty() ? "Connect Trackium Lite to your Gateway" : message);
        shell.addView(title);
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        input.setHint("https://gateway.example");
        input.setText(profile.getString("origin", ""));
        shell.addView(input);
        action("Open Gateway workspace", () -> {
            if (clearingSession) return;
            try {
                GatewayOrigin origin = GatewayOrigin.parse(input.getText().toString());
                if (origin.toString().equals(profile.getString("origin", ""))) {
                    open(origin);
                } else {
                    clearLocalSession(() -> {
                        profile.edit().putString("origin", origin.toString()).apply();
                        open(origin);
                    });
                }
            } catch (IllegalArgumentException error) {
                input.setError("Enter a valid HTTPS origin without a path or credentials");
            }
        });
        action("Forget Gateway and local session", () -> {
            if (clearingSession) return;
            clearLocalSession(() -> showConnection("Local session cleared."));
        });
    }

    private void clearLocalSession(Runnable done) {
        closeBrowser();
        clearingSession = true;
        profile.edit().remove("origin").apply();
        WebStorage.getInstance().deleteAllData();
        CookieManager cookies = CookieManager.getInstance();
        cookies.removeAllCookies(ignored -> {
            cookies.flush();
            clearingSession = false;
            if (!isFinishing() && !isDestroyed()) done.run();
        });
    }

    private void open(GatewayOrigin origin) {
        closeBrowser();
        frame();
        action("Gateway connection", () -> showConnection(""));
        WebView view = new WebView(this);
        browser = view;
        WebSettings settings = view.getSettings();
        // The first-party Gateway workspace needs JavaScript for pairing and navigation.
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString(settings.getUserAgentString() + " TrackiumLiteAndroid/0.1");
        settings.setDomStorageEnabled(true);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieManager.getInstance().setAcceptThirdPartyCookies(view, false);
        view.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView web, WebResourceRequest request) {
                return !origin.permits(request.getUrl().toString(), request.isForMainFrame());
            }

            @Override public WebResourceResponse shouldInterceptRequest(WebView web, WebResourceRequest request) {
                if (origin.permits(request.getUrl().toString(), request.isForMainFrame())) return null;
                return new WebResourceResponse("text/plain", "UTF-8", null);
            }

            @Override public void onReceivedSslError(WebView web, SslErrorHandler handler, SslError error) {
                handler.cancel();
                if (web == browser) showConnection("Gateway TLS validation failed.");
            }

            @Override public void onReceivedError(WebView web, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame() && web == browser) showConnection("Gateway unavailable. Reconnect and retry.");
            }

            @Override public void onReceivedHttpError(WebView web, WebResourceRequest request, WebResourceResponse response) {
                if (request.isForMainFrame() && web == browser) showConnection("Gateway workspace unavailable.");
            }

            @Override public boolean onRenderProcessGone(WebView web, RenderProcessGoneDetail detail) {
                if (web == browser) showConnection("Workspace closed. Reopen the Gateway to resume.");
                return true;
            }
        });
        shell.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        view.loadUrl(origin.workspace());
    }

    private void closeBrowser() {
        if (browser == null) return;
        WebView view = browser;
        browser = null;
        view.stopLoading();
        if (view.getParent() instanceof ViewGroup parent) parent.removeView(view);
        view.destroy();
    }

    @Override public void onBackPressed() {
        if (browser != null && browser.canGoBack()) browser.goBack();
        else if (browser != null) showConnection("");
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        closeBrowser();
        super.onDestroy();
    }
}
