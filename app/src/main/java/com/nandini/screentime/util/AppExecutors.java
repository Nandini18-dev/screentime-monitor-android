package com.nandini.screentime.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Shared background/main-thread executors so DB and platform-stats work never runs on the UI thread. */
public final class AppExecutors {

    private static final AppExecutors INSTANCE = new AppExecutors();

    private final ExecutorService diskIo = Executors.newSingleThreadExecutor();
    private final ExecutorService background = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private AppExecutors() {
    }

    public static AppExecutors getInstance() {
        return INSTANCE;
    }

    public ExecutorService diskIo() {
        return diskIo;
    }

    public ExecutorService background() {
        return background;
    }

    public Executor mainThread() {
        return mainHandler::post;
    }
}
