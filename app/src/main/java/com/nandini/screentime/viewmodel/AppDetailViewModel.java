package com.nandini.screentime.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.nandini.screentime.data.repository.NetworkUsageRepository;
import com.nandini.screentime.data.repository.UsageRepository;
import com.nandini.screentime.util.TimeUtils;

public class AppDetailViewModel extends AndroidViewModel {

    private final LiveData<Long> screenTimeMs;
    private final LiveData<Long> wifiBytes;

    public AppDetailViewModel(@NonNull Application application, @NonNull String packageName) {
        super(application);
        String today = TimeUtils.todayKey();
        screenTimeMs = new UsageRepository(application).getTotalForApp(today, packageName);
        wifiBytes = new NetworkUsageRepository(application).getTotalBytesForApp(today, packageName);
    }

    public LiveData<Long> getScreenTimeMs() {
        return screenTimeMs;
    }

    public LiveData<Long> getWifiBytes() {
        return wifiBytes;
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;
        private final String packageName;

        public Factory(Application application, String packageName) {
            this.application = application;
            this.packageName = packageName;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AppDetailViewModel(application, packageName);
        }
    }
}
