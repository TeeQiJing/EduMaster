package com.practical.edumasters.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.models.Chapter;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentFragment extends Fragment {

    private static final String TAG = "ContentFragment";
    private static final String ARG_CHAPTER_ID = "TxBOqJlU49O4lGmkTqGJ";

    private String chapterId;
    private FirebaseFirestore db;
    private LinearLayout contentContainer;

    public static ContentFragment newInstance(String chapterId) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString("TxBOqJlU49O4lGmkTqGJ", chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            chapterId = "TxBOqJlU49O4lGmkTqGJ";
            Log.d(TAG, "Received Chapter ID: " + chapterId);

        db = FirebaseFirestore.getInstance();
        WebView.setWebContentsDebuggingEnabled(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        contentContainer = rootView.findViewById(R.id.contentContainer);

        if (chapterId != null) {
            loadChapterContent(chapterId);
        } else {
            Toast.makeText(getContext(), "Invalid Chapter ID", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private void loadChapterContent(String chapterId) {
        db.collection("chapters").document(chapterId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                Chapter chapter = Chapter.fromSnapshot(document);
                if (chapter != null) {
                    displayChapterContent(chapter);
                } else {
                    Log.e(TAG, "Chapter parsing failed.");
                }
            } else {
                Log.e(TAG, "Error fetching chapter content", task.getException());
                Toast.makeText(getContext(), "Failed to load chapter content", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChapterContent(Chapter chapter) {
        List<Map<String, Object>> contentList = chapter.getContent();
        if (contentList != null && !contentList.isEmpty()) {
            for (Map<String, Object> contentBlock : contentList) {
                String type = (String) contentBlock.get("type");
                String value = (String) contentBlock.get("value");

                if (type != null && value != null) {
                    switch (type) {
                        case "text":
                            addTextView(value);
                            break;
                        case "image":
                            addImageView(value);
                            break;
                        case "video":
                            addVideoView(value);
                            break;
                        case "code":
                            addCodeView(value);
                            break;
                        default:
                            Log.e(TAG, "Unknown content type: " + type);
                    }
                }
            }
        } else {
            Log.e(TAG, "No content available in chapter.");
        }
    }

    private void addTextView(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);
        contentContainer.addView(textView);
    }

    private void addImageView(String imageUrl) {
        ImageView imageView = new ImageView(getContext());
        Glide.with(this).load(imageUrl).into(imageView);
        imageView.setAdjustViewBounds(true);
        contentContainer.addView(imageView);
    }

    private void addVideoView(String videoUrl) {
        WebView webView = new WebView(getContext());

        // Enable JavaScript and other settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Extract the YouTube video ID from the URL
        String videoId = extractYouTubeVideoId(videoUrl);

        // Construct the YouTube embed URL
        if (videoId != null) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=0&modestbranding=1&rel=0";

            // Load the embed URL in an iframe format
            String iframeHtml = "<html><body style='margin:0; padding:0;'>"
                    + "<iframe width='100%' height='100%' src='" + embedUrl + "' frameborder='0' "
                    + "allowfullscreen></iframe>"
                    + "</body></html>";
            webView.loadData(iframeHtml, "text/html", "utf-8");
        } else {
            Toast.makeText(getContext(), "Invalid video URL", Toast.LENGTH_SHORT).show();
        }

        // Set layout parameters for the WebView
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                600 // Set a fixed height for the video frame
        );
        webView.setLayoutParams(params);

        // Add the WebView to the content container
        contentContainer.addView(webView);
    }

    // Helper function to extract the YouTube video ID
    private String extractYouTubeVideoId(String url) {
        String pattern = "^(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtube\\.com\\/.*v=|youtu\\.be\\/)([\\w-]{11})(?:\\S+)?$";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1); // Return the video ID
        }
        return null; // Return null if no match is found
    }




    private void addCodeView(String code) {
        TextView codeView = new TextView(getContext());
        codeView.setText(code);
        codeView.setPadding(16, 16, 16, 16);
        codeView.setTextSize(14);
        codeView.setBackgroundColor(getResources().getColor(R.color.codeBackground));
        codeView.setTextColor(getResources().getColor(R.color.white));
        contentContainer.addView(codeView);
    }
}
