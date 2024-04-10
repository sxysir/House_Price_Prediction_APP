package com.example.test;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class TipsActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        // Hide the status bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Initialize the VideoView
        videoView = findViewById(R.id.videoView);

        // Set the video resource ID
        int videoResId = R.raw.your_video; // Replace with the actual video file name
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        videoView.setVideoURI(videoUri);

        // Set looping for the video
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("TipsActivity", "Video completed. Starting DashboardActivity.");
                Intent intent = new Intent(TipsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // Optional: finish() the TipsActivity to remove it from the back stack
            }
        });

        // Start playing the video
        videoView.start();

        // Make the VideoView full screen
        makeVideoViewFullscreen(videoView);

        // Skip Button
        ImageButton skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to DashboardActivity
                startActivity(new Intent(TipsActivity.this, DashboardActivity.class));
                finish(); // Optional: finish() the TipsActivity to remove it from the back stack
            }
        });
    }

    private void makeVideoViewFullscreen(VideoView videoView) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP); // Align to the top of the parent
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // Align to the bottom of the parent
        videoView.setLayoutParams(layoutParams);
    }
}
