package com.galaxy.keyboard.input;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxy.keyboard.R;
import com.galaxy.keyboard.helper.GalaxyDatabaseHelper;
import com.galaxy.keyboard.model.PhraseModel;
import com.galaxy.keyboard.widget.GalaxySettingDialog;
import com.galaxy.keyboard.widget.GalaxyTextView;

import java.util.List;

/**
 * Created by anmoluppal on 24/03/18.
 * <p>
 * Custom implementation of Android Keyboard view.
 */

public class GalaxyKeyboardView extends KeyboardView {

    private static Context mContext;
    
    public GalaxyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public GalaxyKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private InputConnection inputConnection;
    
    public void setInputConnection(InputConnection ic) {
        inputConnection = ic;
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        List<Keyboard.Key> keys = getKeyboard().getKeys();
//        for (Keyboard.Key key : keys) {
//            Log.e("___Current Key Loop___", "Key code is " + key.codes[0]);
//            if (key.codes[0] == -96) {
//                Drawable dr = (Drawable) getResources().getDrawable(R.drawable.blue_key_bg);
//                dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
//                dr.draw(canvas);
//
//            } else {
//                Drawable dr = (Drawable) getResources().getDrawable(R.drawable.black_key_bg);
//                dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
//                dr.draw(canvas);
//            }
//
//            Paint paint = new Paint();
//
//            if (key.label != null) {
//                canvas.drawText(key.label.toString(), key.x + (key.width / 2),
//                        key.y + (key.height / 2), paint);
//            } else if (key.icon != null){
//                key.icon.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
//                key.icon.draw(canvas);
//            }
//        }

    }

    public void ShowSettingDialog() {
        Intent myIntent = new Intent(mContext, GalaxySettingDialog.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(myIntent);
    }

}
