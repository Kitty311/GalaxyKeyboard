package com.galaxy.keyboard.input;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

import com.galaxy.keyboard.R;
import com.galaxy.keyboard.widget.GalaxyFeedbackDialog;
import com.galaxy.keyboard.widget.GalaxySettingDialog;

import java.util.List;

/**
 * Created by anmoluppal on 24/03/18.
 * <p>
 * Custom implementation of Android Keyboard view.
 */

public class GalaxyKeyboardView extends KeyboardView {

    public GalaxyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalaxyKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void ShowFeedbackDialog() {
        Intent myIntent = new Intent(getContext(), GalaxyFeedbackDialog.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(myIntent);
    }

    public void ShowSettingDialog() {
        Intent myIntent = new Intent(getContext(), GalaxySettingDialog.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(myIntent);
    }

}
