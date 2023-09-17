package com.example.introexample;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TimerService extends Service {
    private static final long COUNTDOWN_INTERVAL = 1000; // 1 second
    private static final long COUNTDOWN_TIME = 30 * 60 * 1000; // 30 minutes in milliseconds

    private CountDownTimer countDownTimer;
    private long timeLeft; // Track the time left
    private boolean isTimerRunning = false;

    public static final String COUNTDOWN_BROADCAST = "com.example.introexample.countdown_broadcast";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "START_TIMER":
                    startTimer();
                    break;
                case "STOP_TIMER":
                    stopTimer();
                    break;
            }
        }
        return START_STICKY;
    }

    private void startTimer() {
        if (!isTimerRunning) {
            countDownTimer = new CountDownTimer(COUNTDOWN_TIME, COUNTDOWN_INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = millisUntilFinished; // Update time left
                    sendCountDownUpdate(timeLeft);
                }

                @Override
            public void onFinish() {
                timeLeft = 0;
                stopTimer();
                isTimerRunning = false;
            }
        };
        countDownTimer.start();
        isTimerRunning = true;
    }
}

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendCountDownUpdate(long millisUntilFinished) {
        Intent intent = new Intent(COUNTDOWN_BROADCAST);
        intent.putExtra("remaining_time", millisUntilFinished);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
