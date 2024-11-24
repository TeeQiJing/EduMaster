package com.practical.edumasters.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();

    public void setEmail(String userEmail) {
        email.setValue(userEmail);
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public LiveData<String> getUsername() {
        return username;
    }
}
