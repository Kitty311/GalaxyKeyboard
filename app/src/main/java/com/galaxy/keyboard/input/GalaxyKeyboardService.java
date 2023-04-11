package com.galaxy.keyboard.input;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxy.keyboard.R;
import com.galaxy.keyboard.flexbox.GalaxyFbLayoutManager;
import com.galaxy.keyboard.helper.GalaxyAppHelper;
import com.galaxy.keyboard.helper.GalaxyDatabaseHelper;
import com.galaxy.keyboard.model.PhraseModel;
import com.galaxy.keyboard.helper.GalaxyConstant;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

public class GalaxyKeyboardService extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private GalaxyKeyboardView kv;
    private View closeButton;
    private Keyboard keyboard;

    private static Context context;
    private GalaxyDatabaseHelper gdh;

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
    }

    @Override
    public AbstractInputMethodImpl onCreateInputMethodInterface() {
        return super.onCreateInputMethodInterface();
    }

    @Override
    public boolean onEvaluateInputViewShown() {
        Log.e("___Start Input___", "Input Started");
        return super.onEvaluateInputViewShown();
    }

    @Override
    public boolean onShowInputRequested(int flags, boolean configChange) {
        return super.onShowInputRequested(flags, configChange);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        gdh = new GalaxyDatabaseHelper(context);
    }

    private boolean isCaps = false;

    @Override
    public void onPress(int i) {
        Log.e("___Pressed Key___", String.valueOf(i));
    }

    @Override
    public void onRelease(int i) {
        Log.e("___Released Key___", String.valueOf(i));
    }

    private void finishWriting() {
        ic.commitText(curText, 0);
    }

    private Keyboard.Key findKey(Keyboard keyboard, int primaryCode) {
        for (Keyboard.Key key : keyboard.getKeys()) {
            if (key.codes[0] == primaryCode) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void onKey(int i, int[] ints) {

        Log.e("___Current Key___", String.valueOf(i));
        isPredictionMode = true;

        ic = getCurrentInputConnection();
        switch (i) {
            case GalaxyConstant.FINISH_KEY_CODE:
                finishWriting();
                break;
            case GalaxyConstant.BACKSPACE_KEY_CODE:
                if (curTempText.length() > 0)
                curTempText = curTempText.substring(0, curTempText.length() - 1);
                break;
            case GalaxyConstant.ENTER_KEY_CODE:
                curTempText += "\n";
                break;
            case GalaxyConstant.SWITCH_NUMBER_PAD_KEY_CODE:
                keyboard = new Keyboard(this, R.xml.back_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.SWITCH_CHARACTER_PAD_KEY_CODE:
                keyboard = new Keyboard(this, R.xml.front_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.SEND_FEEDBACK_KEY_CODE:
                kv.ShowFeedbackDialog();
                break;
            case GalaxyConstant.SWITCH_LANGUAGE_KEY_CODE:
                GalaxyAppHelper.Companion.SwitchKeyboard(context);
                break;
            case GalaxyConstant.SETTING_KEY_CODE:
                kv.ShowSettingDialog();
                break;
            case GalaxyConstant.SWITCH_DATA_PACK_KEY_CODE:
                Keyboard.Key key = findKey(keyboard, i);
                if (GalaxyAppHelper.Companion.GetCurrentDataPack(context)) {
                    key.icon = getDrawable(R.drawable.ab43_office);
                } else {
                    key.icon = getDrawable(R.drawable.ab43_standard);
                }
                GalaxyAppHelper.Companion.SetCurrentDataPack(context, !GalaxyAppHelper.Companion.GetCurrentDataPack(context));
                break;
            default:
                char code = (char) i;
                curTempText += code;
        }

        doGalaxyOperation();

    }

    private String curTempText = ""; // Text which is typed for prediction or follower
    private int curTempTextPos = 0; // Position which curTempText should be written
    private String curText = ""; // Text which is written on text field
    private String followerText = ""; // Text which is used for follower
    private boolean isPredictionMode = true;

    private InputConnection ic;

    // Here goes all the logic which predict words, show text to the our text field and so on
    private void doGalaxyOperation() {

        if (isPredictionMode) {
            curText = curText.substring(0, curTempTextPos);
            curText += curTempText;
        }

        GalaxyPredictAdapter gpa = new GalaxyPredictAdapter(
                isPredictionMode
                        ? gdh.readPrediction(curTempText)
                        : gdh.readFollower(followerText)
        );
        predictListView.setAdapter(gpa);
        GalaxyPredictAdapter.setOnItemClickListener(new GalaxyPredictAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                String selText = ((TextView)v.findViewById(R.id.wordText)).getText().toString();
                curText = curText.substring(0, curTempTextPos);
                curText += selText;
                curTempText = "";
                curTempTextPos = curText.length();
                if (!isPredictionMode)
                    followerText = selText;
                doGalaxyOperation();
                isPredictionMode = false;
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
        textField.setText(curText);
    }

    // This is adapter that shows prediction words
    public static class GalaxyPredictAdapter extends RecyclerView.Adapter<GalaxyPredictAdapter.GalaxyViewHolder> {

        private static ClickListener clickListener;
        private List<PhraseModel> mList;

        public GalaxyPredictAdapter(List<PhraseModel> list) {
            this.mList = list;
        }

        @NonNull
        @Override
        public GalaxyPredictAdapter.GalaxyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GalaxyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_prediction, null));
        }

        @Override
        public void onBindViewHolder(@NonNull GalaxyPredictAdapter.GalaxyViewHolder holder, int position) {
            holder.setWordText(mList.get(position).getMPredict());
            holder.setNumText((mList.size() - position) + "");
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public static void setOnItemClickListener(ClickListener clickListener) {
            GalaxyPredictAdapter.clickListener = clickListener;
        }

        // If you wanna change prediction word style, work here
        public static class GalaxyViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            private TextView wordText;
            private TextView numText;
            public GalaxyViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                wordText = itemView.findViewById(R.id.wordText);
                numText = itemView.findViewById(R.id.numText);
                wordText.setTextSize(GalaxyAppHelper.Companion.GetCurrentAppTextSize(context));
                numText.setTextSize(GalaxyAppHelper.Companion.GetCurrentAppTextSize(context));
            }
            public void setWordText(String s) {
                wordText.setText(s);
            }
            public void setNumText(String s) {
                numText.setText(s);
            }
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
            @Override
            public boolean onLongClick(View v) {
                clickListener.onItemLongClick(getAdapterPosition(), v);
                return false;
            }
        }

        public interface ClickListener {
            void onItemClick(int position, View v);
            void onItemLongClick(int position, View v);
        }

    }

    @Override
    public void onText(CharSequence charSequence) {
        Log.e("Current Text", charSequence.toString());
    }

    @Override
    public void swipeLeft() {
        Log.e("___Swipe Left___", "DONE");
    }

    @Override
    public void swipeRight() {
        Log.e("___Swipe Right___", "DONE");
    }

    @Override
    public void swipeDown() {
        Log.e("___Swipe Down___", "DONE");
    }

    @Override
    public void swipeUp() {
        Log.e("___Swipe Up___", "DONE");
    }

    private RecyclerView predictListView;
    private TextView textField;
    private boolean isCreated = false;

    // It is called when the keyboard appears
    @Override
    public View onCreateInputView() {
        Log.e("___Galaxy Keyboard___", "CREATED");
        isCreated = true;
        View galaxyView = getLayoutInflater().inflate(R.layout.galaxy_keyboard, null);
        predictListView = galaxyView.findViewById(R.id.predictListView);
        GalaxyFbLayoutManager layoutManager = new GalaxyFbLayoutManager(context);
        layoutManager.setFlexDirection(FlexDirection.ROW_REVERSE);
        layoutManager.setFlexWrap(FlexWrap.WRAP_REVERSE);
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        predictListView.setLayoutManager(layoutManager);

        textField = galaxyView.findViewById(R.id.textField);
        kv = galaxyView.findViewById(R.id.keyboardView);
        keyboard = new Keyboard(this, R.xml.front_keypad);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return galaxyView;
    }

}
