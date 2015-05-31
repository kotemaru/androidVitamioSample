package org.kotemaru.android.rtmpclient;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	private final static String KEY_POSITION = "KEY_POSITION";

	private VideoView mVideoView;
	private PlayerListener mPlayerListener;
	private ProgressBar mProgressBar;
	private long mPosition = 0; // ms

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this)) return; // Vitamio で必須

		setContentView(R.layout.activity_main);

		mVideoView = (VideoView) findViewById(R.id.videoView);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mPlayerListener = new PlayerListener();

		mVideoView.setOnInfoListener(mPlayerListener);
		mVideoView.setOnBufferingUpdateListener(mPlayerListener);
		mVideoView.setMediaController(new MediaController(this));

		if (savedInstanceState != null) {
			mPosition = savedInstanceState.getLong(KEY_POSITION);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String path = intent.getDataString();
		Log.d("DEBUG", "path=" + path);
		if (path == null) return;
		mVideoView.setVideoPath(path);
		mVideoView.requestFocus();
		mVideoView.seekTo(mPosition);
		mVideoView.start();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_POSITION, mVideoView.getCurrentPosition());
	}

	private class PlayerListener implements OnInfoListener, OnBufferingUpdateListener {
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				if (mVideoView.isPlaying()) {
					mVideoView.pause();
					mProgressBar.setVisibility(View.VISIBLE);
				}
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				mVideoView.start();
				mProgressBar.setVisibility(View.INVISIBLE);
				break;
			case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
				break;
			}
			return true;
		}
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			Log.d("DEBUG", "buff=" + percent);
		}
	}
}
