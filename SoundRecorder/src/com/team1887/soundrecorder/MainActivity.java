package com.team1887.soundrecorder;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * recording 
 * saving 
 * playing 
 * stop
 * open files
 *
 */

public class MainActivity extends Activity implements OnCompletionListener, OnClickListener {

	private String defaultPath = null;
	private int recorderCounter = 0;
	
	private boolean isPlaying = false;
	private boolean isRecording = false;
	
	private Timer timer = null;
	private TimerTask tTask = null;
	private TextView tv_showTime;
	
	private MediaPlayer mPlayer;
	private ImageButton btn_play;
	
	private MediaRecorder mRecorder;
	private ImageButton btn_recorder;
	
	private ImageButton btn_stop;
	private ImageButton btn_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();    
    }

	private void initView() {
		//play button: play sound if have any in default folder
        btn_play = (ImageButton) findViewById(R.id.ibtn_play);
        //stop button: stop the action either playing or recording
        btn_stop = (ImageButton) findViewById(R.id.ibtn_stop);
        //record button: record and save the sound file into default folder
        btn_recorder = (ImageButton) findViewById(R.id.ibtn_record);
        //open file
        btn_list = (ImageButton) findViewById(R.id.ibtn_file);
        //show how long have been recording
        tv_showTime = (TextView) findViewById(R.id.tv_showSoundtime);
               
        btn_play.setOnClickListener(this);        
        btn_recorder.setOnClickListener(this);        
        btn_stop.setOnClickListener(this);
        btn_list.setOnClickListener(this);
	}
    
    @Override
    public void onCompletion(MediaPlayer mp) {
    	stopPlaying();		
    	isPlaying = false;
    	btn_play.setImageResource(R.drawable.ic_play);
    }
    
    @Override
    public void onClick(View v) {
    	switch(v.getId()) {
    	case R.id.ibtn_play:
    		onPlay(isPlaying);
			isPlaying = !isPlaying;
			
			if(isPlaying) {
				btn_play.setImageResource(R.drawable.ic_pause);
			} else {
				btn_play.setImageResource(R.drawable.ic_play);
			}
			break;
    	case R.id.ibtn_record:
    		onRecord(isRecording);
			isRecording = !isRecording;
			if(isRecording) {
				btn_recorder.setImageResource(R.drawable.ic_recording);
			} else {
				btn_recorder.setImageResource(R.drawable.ic_record);
			}
			break;
    	case R.id.ibtn_stop:
    		Stop();	
    		isRecording = false;
    		isPlaying = false;

			btn_recorder.setImageResource(R.drawable.ic_record);
			btn_play.setImageResource(R.drawable.ic_play);
    		break;
    	case R.id.ibtn_file:
    		//open local folder
    		openFolder();
    		break;
    	}
    	
    }
    
	private void onPlay(boolean isPlaying) {
		if(!isPlaying) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}
	
	private void onRecord(boolean isRecording) {
		if(!isRecording) {
			startRecording();
		} else {
			stopRecording();
		}
	}
	
	private void Stop() {
			stopPlaying();
			stopRecording();
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(getdefaultSavePath());
			mPlayer.setOnCompletionListener(this);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopPlaying() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(getdefaultSavePath());
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startTiming();
		recorderCounter++;
	}

	private void stopRecording() {
		if (mRecorder != null) {
			stopTiming();
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
	
	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_showTime.setText(msg.arg1 + "");
			startTiming();
		};
	};

	private int i = 0;
	private void startTiming() {
		timer = new Timer();
		tTask = new TimerTask() {
			@Override
			public void run() {
				i++;
				Message msg = mhandler.obtainMessage();
				msg.arg1 = i;
				mhandler.sendMessage(msg);
			}
		};
		timer.schedule(tTask, 100);
	}
	
	private void stopTiming() {
		timer.cancel();
	}
	
	private String getdefaultSavePath() {
		//get path to the sdcard
		defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String savefile = String.format("/%d.mp3", recorderCounter);
		defaultPath += savefile;
		return defaultPath;
	}
	
	private String getOpenPath() {
//		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		if(defaultPath.isEmpty()) return getdefaultSavePath();
		return defaultPath;
	}
	
	
	public void openFolder()
	{
		File file = new File(Environment.getExternalStorageDirectory(),"soundRecorder");
	
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setDataAndType(Uri.fromFile(file), "*/*");
		startActivity(intent);
	}
}
