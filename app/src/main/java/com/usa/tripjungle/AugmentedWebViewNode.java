package com.usa.tripjungle;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;
public class AugmentedWebViewNode extends AnchorNode {
    private static CompletableFuture<ViewRenderable> viewFuture;
    LinearLayout infoView;
    public AugmentedWebViewNode(Context context, String htmlString, Vector3 pose, String title) {
        // Upon construction, start loading the viewFuture
        if (viewFuture == null) {
            Node node = new Node();
            node.setParent(this);
            node.setEnabled(false);
            node.setLocalPosition(new Vector3(pose));
            ViewRenderable.builder()
                    .setView(context, R.layout.ar_webview_layout)
                    .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                    .build()
                    .thenAccept(
                            (renderable) -> {
                                node.setRenderable(renderable);
                                node.setEnabled(true);
                                infoView = renderable.getView().findViewById(R.id.infoView);
                                WebView webView = renderable.getView().findViewById(R.id.webview);
                                TextView textView = renderable.getView().findViewById(R.id.webViewTitle);
                                ImageView infoClose = renderable.getView().findViewById(R.id.infoClose);
                                webView.setBackgroundColor(0);
                                String dataHeader = title;
                                String dataView = "<body style=\"margin: 0 !important;padding: 0 !important;\"><div id='viewContent' style='font-weight: 1;flex-direction: column; background: rgba(0,0,0,0); color:rgba(150,150,150,0.8);display:flex'>" +
                                    "<h4>" + htmlString+"</h4>" +
                                    "</div></body>";
                                textView.setText(dataHeader);
                                webView.loadData(dataView, "text/html; charset=utf-8", "UTF-8");
                                infoClose.setOnClickListener(v -> {
//                                    Toast.makeText(context, "matchined ", Toast.LENGTH_SHORT).show();
                                    infoClose.setVisibility(View.GONE);
                                    webView.setVisibility(View.GONE);
                                });
                                textView.setOnClickListener(v -> {
                                    infoClose.setVisibility(View.VISIBLE);
                                    webView.setVisibility(View.VISIBLE);
                                });
                            })
                    .exceptionally(
                            (throwable) -> {
                                throw new AssertionError("Could not load card view.", throwable);
                            }
                    );
//            node.setRenderable(viewFuture.getNow(null));
//
//            Node tigerTitleNode = new Node();
//            tigerTitleNode.setParent(this);
//            tigerTitleNode.setEnabled(false);
//            tigerTitleNode.setLocalPosition(new Vector3(0.0f, 1.0f, 0.0f));
//            ViewRenderable.builder()
//                    .setView(context, R.layout.ar_webview_layout)
//                    .build()
//                    .thenAccept(
//                            (renderable) -> {
//                                tigerTitleNode.setRenderable(renderable);
//                                tigerTitleNode.setEnabled(true);
//                            })
//                    .exceptionally(
//                            (throwable) -> {
//                                throw new AssertionError("Could not load card view.", throwable);
//                            }
//                    );
//            tigerTitleNode.setRenderable(viewFuture.getNow(null));
        }

    }
}
