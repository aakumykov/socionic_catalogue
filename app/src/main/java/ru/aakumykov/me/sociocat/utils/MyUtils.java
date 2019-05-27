package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.login.Login_View;

import static android.content.Context.CLIPBOARD_SERVICE;

public final class MyUtils {

    private final static String TAG = "MyUtils";

    private MyUtils() {}

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static void enable(View view) {
        view.setEnabled(true);
    }

    public static void disable(View view) {
        view.setEnabled(false);
    }

    public static String mime2ext(String mimeType) {
        if (null == mimeType) return null;

        Pattern pattern = Pattern.compile("^image/([a-z]+)$");
        Matcher matcher = pattern.matcher(mimeType);

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String detectImageType(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeType = contentResolver.getType(uri);
        if (null == mimeType)
            mimeType = Config.DEFAULT_IMAGE_TYPE;
        return mimeTypeMap.getExtensionFromMimeType(mimeType);
    }

    public static String detectImageType(Context context, String imageURL) {
        imageURL = imageURL.trim();
        imageURL = imageURL.toLowerCase();

        Pattern pattern = Pattern.compile("^.+\\.([a-z]+)$");
        Matcher matcher = pattern.matcher(imageURL); // запомни, РВ должно соответствовать строке целиком!

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return Config.DEFAULT_IMAGE_TYPE;
        }
    }

//    public static Card snapshot2card(DataSnapshot dataSnapshot) throws IllegalArgumentException {
//        Card card_edit = dataSnapshot.getValue(Card.class);
//        // TODO: протестировать с card_edit == null
//        if (null != card_edit) {
//            card_edit.setKey(dataSnapshot.getKey());
//        } else {
//            throw new IllegalArgumentException("Card object is null");
//        }
//        return card_edit;
//    }

//    public static String getMimeType(String url) {
//        String type = null;
//        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//        if (extension != null) {
//            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        }
//        return type;
//    }

    public static <K, V> Map<K, V> mapDiff(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Map<K, V> difference = new HashMap<>();
        difference.putAll(left);
        difference.putAll(right);
        difference.entrySet().removeAll(right.entrySet());
        return difference;
    }



//    public static String getMimeTypeFromIntent(@Nullable Intent intent) throws IllegalArgumentException {
//
//        if (null == intent) throw new IllegalArgumentException("Supplied Intent is null");
//
//        ClipData clipData = intent.getClipData();
//        if (null == clipData) throw new IllegalArgumentException("ClipData is null");
//
//        ClipDescription clipDescription = clipData.getDescription();
//        if (null == clipDescription) throw new IllegalArgumentException("ClipDescription is null");
//
//        return clipDescription.getMimeType(0);
//    }

    public static String getMimeTypeFromIntent(@Nullable Intent intent) {

        if (null == intent) return null;

        ClipData clipData = intent.getClipData();
        if (null == clipData) return null;

        ClipDescription clipDescription = clipData.getDescription();
        if (null == clipDescription) return null;

        String mimeType = clipDescription.getMimeType(0);

        // TODO: проверять с помощью regexp-ов
//        if (mimeType.matches("^[a-z]+\\/[^a-z0-9.+-]+$")) return mimeType;
//        else

        return mimeType;
    }

    public static String cutToLength(String text, Integer maxLength) {
        if (null == text) {
            Log.e(TAG, "You must supply input text.");
            return null;
        }
        if (null == maxLength) {
            Log.e(TAG, "You must supply maxLength.");
            maxLength = text.length();
        }
        return text.substring(0, Math.min(text.length(), maxLength));
    }


    public static void showKeyboard(Context ctx, EditText editText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, 0);
        }
    }

    public static void showKeyboardOnFocus(Context context, EditText editTextView) {

        editTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != imm)
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        editTextView.requestFocus();
    }

    public static void hideKeyboard(Context ctx, EditText editText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static boolean isEmailCorrect(String email) {
        Pattern pattern = Pattern.compile("^([a-z0-9+_]+[.-]?)*[a-z0-9]+@([a-z0-9]+[.-]?)*[a-z0-9]+\\.[a-z]+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static <T> HashMap<T, Boolean> list2hashMap(Set<T> list, boolean defaultValue) {
        HashMap<T,Boolean> hashMap = new HashMap<>();
        for(T key : list)
            hashMap.put(key, defaultValue);
        return hashMap;
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static <T> String object2string(Context context, T arg) {
        return (arg instanceof Integer) ? context.getResources().getString((Integer)arg) : String.valueOf(arg);
    }

    public static String getClipboardText(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (null != clipData && clipData.getItemCount() > 0) {
            ClipData.Item item = clipData.getItemAt(0);
            return item.getText().toString();
        } else {
            return null;
        }
    }

    public static int getOrientation(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation;
    }

    public static boolean isPortraitOrientation(Context context) {
        return Configuration.ORIENTATION_PORTRAIT == getOrientation(context);
    }

    public static void showCustomToast(Context context, String message) {

        LayoutInflater myInflater = LayoutInflater.from(context);
        View view = myInflater.inflate(R.layout.toast, null);

        TextView textView = view.findViewById(R.id.textView);
        textView.setText(message);

        Toast mytoast = new Toast(context);
        mytoast.setView(view);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }

    public static String stackTrace2String(StackTraceElement[] stackTraceElements) {
        String stackTrace = "";
        for (StackTraceElement element : stackTraceElements)
            stackTrace += element.toString() + "\n";
        return stackTrace;
    }

    /*public static String getString(Context context, int msgId) {
        return context.getResources().getString(msgId);
    } */

    /*public static void requestLogin(Context context, Intent proceedIntent) {
        Intent intent = new Intent(context, Login_View.class);
        intent.setAction(Constants.ACTION_LOGIN_REQUEST);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(Intent.EXTRA_INTENT, proceedIntent);
        context.startActivity(intent);
    }*/
}
