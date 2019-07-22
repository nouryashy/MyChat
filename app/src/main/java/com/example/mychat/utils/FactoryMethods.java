package com.example.mychat.utils;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.mychat.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

public class FactoryMethods {
    public static boolean isValiedEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());

    }

    public static boolean isValedPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            return false;
        }
        return true;
    }

    public static void showProgressBar(ProgressBar progressBar, Button button) {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.ic_register_button_clicked);

    }

    public static void hideProgressBar(ProgressBar progressBar, Button button) {
        progressBar.setVisibility(View.GONE);
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.ic_register_button);
    }

    public static long getCurrentTimeStamp() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public static String getDate(Long time) {

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd/MM/yyyy hh:mm a", cal).toString();
        return date;
    }
}
