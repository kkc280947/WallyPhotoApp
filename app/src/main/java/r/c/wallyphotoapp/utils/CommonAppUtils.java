package r.c.wallyphotoapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import r.c.wallyphotoapp.R;

public class CommonAppUtils {

    public static boolean isListEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            return cm.getActiveNetworkInfo() != null;
        }
        return false;
    }

    public static void hideKeyboard(Activity activity){
        if(activity != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void showInternetError(Context context, DialogInterface.OnClickListener postiveClickListener){
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.text_no_internet_connection))
                .setMessage(context.getString(R.string.text_content_no_internet_connection))
                .setCancelable(false)
                .setPositiveButton(context.getString(android.R.string.ok),postiveClickListener)
                .create()
                .show();
    }

    public static String getCurrentUserUID(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fireBaseUser = mAuth.getCurrentUser();
        if(fireBaseUser!=null) {
            return fireBaseUser.getUid();
        }else {
            return "";
        }
    }
}

