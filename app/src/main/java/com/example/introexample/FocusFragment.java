package com.example.introexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class FocusFragment extends Fragment {
    private View view;
    private ProgressBar progressBar;
    private Button startButton;
    private CountDownTimer countDownTimer;
    private Boolean countDownWorking;
    private TextView tvtimer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_focus, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        startButton = view.findViewById(R.id.startButton);
        tvtimer = view.findViewById(R.id.tvtimer);
        countDownWorking = false;
        if (countDownWorking!=null){
            if (countDownWorking==false){
                startButton.setText("START FOCUSING");
            }else {
                startButton.setText("STOP FOCUSING");
            }
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countDownWorking) {
                    Intent startTimerIntent = new Intent(getActivity(), TimerService.class);
                    startTimerIntent.setAction("START_TIMER");
                    getActivity().startService(startTimerIntent);
                    startButton.setText("FOCUS");
                    countDownWorking = true;
                } else {
                    Intent stopTimerIntent = new Intent(getActivity(), TimerService.class);
                    stopTimerIntent.setAction("STOP_TIMER");
                    getActivity().startService(stopTimerIntent);
                    countDownWorking = false;
                    progressBar.setProgress(30 * 60 * 1000);
                    startButton.setText("FOCUS");
                    tvtimer.setText("30:00");
                }
            }
        });
        return view;
    }

    private BroadcastReceiver countdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TimerService.COUNTDOWN_BROADCAST)) {
                long remainingTime = intent.getLongExtra("remaining_time", 0);
                progressBar.setProgress((int) remainingTime);
                long minutes = (remainingTime / 1000) / 60;
                long seconds = (remainingTime / 1000) % 60;
                tvtimer.setText(String.format("%02d:%02d", minutes, seconds));
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (countDownWorking!=null){
            if (countDownWorking==false){
                startButton.setText("FOCUS");
            }else {
                startButton.setText("FOCUS");
            }
        }
        IntentFilter filter = new IntentFilter(TimerService.COUNTDOWN_BROADCAST);
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(countdownReceiver, filter);
    }


    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(countdownReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer!=null){
            countDownWorking=false;
            countDownTimer.cancel();
        }
    }
}