package com.galaxy.keyboard.input;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
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
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

public class GalaxyKeyboardService extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private GalaxyKeyboardView kv;
    private Keyboard keyboard;

    private InputConnection ic;

    private KEYPAD_MODE curKeypadMode = KEYPAD_MODE.BURMESE_FRONT_KEY_PAD;
    private enum KEYPAD_MODE {
        BURMESE_FRONT_KEY_PAD,
        BURMESE_BACK_KEY_PAD,
        ENGLISH_FRONT_KEY_PAD,
        ENGLISH_BACK_KEY_PAD
    }

    private static Context context;
    private static GalaxyDatabaseHelper gdh;

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

    @Override
    public void onPress(int i) {
        Log.e("___Pressed Key___", String.valueOf(i));
    }

    @Override
    public void onRelease(int i) {
        Log.e("___Released Key___", String.valueOf(i));
    }

    private Keyboard.Key findKey(Keyboard keyboard, int primaryCode) {
        for (Keyboard.Key key : keyboard.getKeys()) {
            if (key.codes[0] == primaryCode) {
                return key;
            }
        }
        return null;
    }

    private boolean isUserTextMode = false;

    @Override
    public void onKey(int i, int[] ints) {

        Log.e("___Current Key___", String.valueOf(i));
        isPredictionMode = true;

        ic = getCurrentInputConnection();
        switch (i) {
            case GalaxyConstant.FINISH_KEY_CODE:
                Keyboard.Key kk = findKey(keyboard, i);
                if (isUserTextMode) {
                    kk.icon = getDrawable(R.drawable.a10);
                    userTextBox.setVisibility(View.GONE);
                    isUserTextMode = false;
                    if (userTextBox.getText().toString().isEmpty()) return;
                    PhraseModel item = new PhraseModel();
                    item.setMInput(userTextBox.getText().toString());
                    item.setMPredict(userTextBox.getText().toString());
                    item.setMIsUserText(1);
                    gdh.insertPrediction(item);
                    curText = "";
                    curUserText = "";
                    userTextBox.setText(curUserText);
                    userEntryBox.setText(curText);
                    predictListView.setAdapter(null);
                } else {
                    kk.icon = getDrawable(R.drawable.a10_);
                    userTextBox.setVisibility(View.VISIBLE);
                    curText = "";
                    curUserText = "";
                    userTextBox.setText(curUserText);
                    userEntryBox.setText(curText);
                    predictListView.setAdapter(null);
                    isUserTextMode = true;
                }
                break;
            case GalaxyConstant.BACKSPACE_KEY_CODE:
                if (isUserTextMode) {
                    if (curUserText.length() > 0)
                        curUserText = curUserText.substring(0, curUserText.length() - 1);
                }
                if (curText.length() > 0) {
                    curText = curText.substring(0, curText.length() - 1);
                }
                else if (!isUserTextMode)
                    ic.deleteSurroundingText(1, 0);
                doGalaxyOperation();
                break;
            case GalaxyConstant.ENTER_KEY_CODE:
                if (isUserTextMode) {
                    curUserText += "\n";
                    curText = "";
                    userEntryBox.setText(curText);
                } else {
                    submitInUnicode();
                    ic.commitText("\n", 0);
                }
                doGalaxyOperation();
                break;
            case GalaxyConstant.SPACE_KEY_CODE:
                if (isUserTextMode) {
                    curUserText += " ";
                    curText = "";
                    userEntryBox.setText(curText);
                } else {
                    ic.commitText(" ", 0);
                }
                doGalaxyOperation();
                break;
            case GalaxyConstant.SWITCH_BURMESE_BACK_PAD_KEY_CODE:
                curKeypadMode = KEYPAD_MODE.BURMESE_BACK_KEY_PAD;
                keyboard = new Keyboard(this, R.xml.bur_back_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.SWITCH_BURMESE_FRONT_PAD_KEY_CODE:
                curKeypadMode = KEYPAD_MODE.BURMESE_FRONT_KEY_PAD;
                keyboard = new Keyboard(this, R.xml.bur_front_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.SWITCH_ENGLISH_BACK_PAD_KEY_CODE:
                curKeypadMode = KEYPAD_MODE.ENGLISH_BACK_KEY_PAD;
                keyboard = new Keyboard(this, R.xml.eng_back_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.SWITCH_ENGLISH_FRONT_PAD_KEY_CODE:
                curKeypadMode = KEYPAD_MODE.ENGLISH_FRONT_KEY_PAD;
                keyboard = new Keyboard(this, R.xml.eng_front_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.SWITCH_ENGLISH_PAD_KEY_CODE:
//                GalaxyAppHelper.Companion.SwitchKeyboard(context);
                curKeypadMode = KEYPAD_MODE.ENGLISH_FRONT_KEY_PAD;
                keyboard = new Keyboard(this, R.xml.eng_front_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.SWITCH_BURMESE_PAD_KEY_CODE:
//                GalaxyAppHelper.Companion.SwitchKeyboard(context);
                curKeypadMode = KEYPAD_MODE.BURMESE_FRONT_KEY_PAD;
                keyboard = new Keyboard(this, R.xml.bur_front_keypad);
                kv.setKeyboard(keyboard);
                break;
            case GalaxyConstant.USER_TEXT_KEY_CODE:
                doKagayaOperation();
                break;
            case GalaxyConstant.SETTING_KEY_CODE:
                kv.ShowSettingDialog();
                break;
            case GalaxyConstant.SWITCH_DATA_PACK_KEY_CODE:
                Keyboard.Key key = findKey(keyboard, i);
                if (GalaxyAppHelper.Companion.GetCurrentDataPack(context)) {
                    key.icon = getDrawable(R.drawable.abcd43_office);
                } else {
                    key.icon = getDrawable(R.drawable.abcd43_standard);
                }
                GalaxyAppHelper.Companion.SetCurrentDataPack(context, !GalaxyAppHelper.Companion.GetCurrentDataPack(context));
                break;
            default:
                char code = (char) i;
                curTextUnicode = i;

                boolean isSpecialSymbol = curTextUnicode >= 193 && curTextUnicode <= 225;
                if (isSpecialSymbol) {
                    doKittyOperation(code + "");
                    return;
                }

                boolean isBurmeseNumber = curTextUnicode >= 4160 && curTextUnicode <= 4169;

                if (isUserTextMode) {
                    curUserText += code;
                } else if (curKeypadMode == KEYPAD_MODE.ENGLISH_BACK_KEY_PAD || curKeypadMode == KEYPAD_MODE.ENGLISH_FRONT_KEY_PAD || isBurmeseNumber) {
                    ic.commitText(code + "", 0);
                    return;
                }
                curText += code;
                doGalaxyOperation();
        }
    }

    private String curText = ""; // Text which is written on user entry box, also can be used for prediction
    private String curUserText = ""; // Same as @curText but only works when user memory mode is on
    private int curTextUnicode = 0; // Unicode of pressed key
    private String followerText = ""; // Text which is used for follower
    private boolean isPredictionMode = true;
    private void submitInUnicode() {
        String unicodeText = "";
        for (char s : curText.toCharArray()) {
            unicodeText += gdh.readUnicodeFromSancode(s + "").getMUnicode();
        }
        if (isUserTextMode) {
            String userEntryBoxString = userEntryBox.getText().toString();
            curUserText = curUserText.substring(0, curUserText.length() - userEntryBoxString.length());
            curUserText += curText;
            userTextBox.setText(curUserText);
        } else {
            ic.commitText(unicodeText, 0);
        }
        curText = "";
        userEntryBox.setText(curText);
    }

    private void submitUserText() {
        if (isUserTextMode) {
            curUserText += curText;
            userTextBox.setText(userTextBox.getText().toString() + curText);
        } else {
            String unicodeText = "";
            for (char s : curText.toCharArray()) {
                unicodeText += gdh.readUnicodeFromSancode(s + "").getMUnicode();
            }
            ic.commitText(unicodeText, 0);
        }
        curText = "";
        userEntryBox.setText(curText);
        predictListView.setAdapter(null);
    }

    private void doGalaxyOperation() {

        if (isPredictionMode) {
            userEntryBox.setText(curText);
        }

        if (isUserTextMode) {
            userTextBox.setText(curUserText);
        }

        List<PhraseModel> phraseList = isPredictionMode
                ? gdh.readPrediction(curText, 0)
                : gdh.readFollower(followerText);
        GalaxyPredictAdapter gpa = new GalaxyPredictAdapter(phraseList);
        predictListView.setAdapter(gpa);
        GalaxyPredictAdapter.setOnItemClickListener(new GalaxyPredictAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                PhraseModel item = phraseList.get(position);
                if (isPredictionMode)
                    gdh.increasePredictionFrequency(item.getMId());
                else
                    gdh.increaseFollowerFrequency(item.getMId());
                String selText = ((TextView)v.findViewById(R.id.wordText)).getText().toString();
                followerText = selText.charAt(selText.length() - 1) + "";
                curText = selText;
                isPredictionMode = false;
                doGalaxyOperation();
                submitInUnicode();
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
    }

    private void doKagayaOperation() {

        List<PhraseModel> phraseList = gdh.readPrediction(curText, 1);
        GalaxyPredictAdapter gpa = new GalaxyPredictAdapter(phraseList);
        predictListView.setAdapter(gpa);
        GalaxyPredictAdapter.setOnItemClickListener(new GalaxyPredictAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                curText = ((TextView)v.findViewById(R.id.wordText)).getText().toString();
                submitUserText();
            }

            @Override
            public void onItemLongClick(int position, View v) {
//                phraseList.remove(position);
//                gpa.notifyItemRemoved(position);
            }
        });
    }

    private void doKittyOperation(String symbol) {

        List<PhraseModel> phraseList = isPredictionMode
                ? gdh.readPrediction(symbol, 0)
                : gdh.readFollower(followerText);
        GalaxyPredictAdapter gpa = new GalaxyPredictAdapter(phraseList);
        predictListView.setAdapter(gpa);
        GalaxyPredictAdapter.setOnItemClickListener(new GalaxyPredictAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                PhraseModel item = phraseList.get(position);
                if (isPredictionMode)
                    gdh.increasePredictionFrequency(item.getMId());
                else
                    gdh.increaseFollowerFrequency(item.getMId());
                String selText = ((TextView)v.findViewById(R.id.wordText)).getText().toString();
                followerText = selText.charAt(selText.length() - 1) + "";
                curText = selText;
                isPredictionMode = false;
                doGalaxyOperation();
                submitInUnicode();
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });
    }

    // This is adapter that shows prediction words
    public static class GalaxyPredictAdapter extends RecyclerView.Adapter<GalaxyPredictAdapter.GalaxyViewHolder> {

        private static ClickListener clickListener;
        private static List<PhraseModel> mList;

        public GalaxyPredictAdapter(List<PhraseModel> list) {
            mList = list;
        }

        @NonNull
        @Override
        public GalaxyPredictAdapter.GalaxyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_prediction, null);
            GalaxyViewHolder viewHolder = new GalaxyViewHolder(view);
            return viewHolder;
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

            private final TextView wordText;
            private final TextView numText;
            public GalaxyViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                wordText = itemView.findViewById(R.id.wordText);
                numText = itemView.findViewById(R.id.numText);
                wordText.setTextSize(GalaxyAppHelper.Companion.GetCurrentAppTextSize(context));
                numText.setTextSize(GalaxyAppHelper.Companion.GetCurrentAppTextSize(context));
                wordText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null) {
                            clickListener.onItemClick(getAdapterPosition(), view);
                        }
                    }
                });
                wordText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (clickListener != null) {
                            clickListener.onItemLongClick(getAdapterPosition(), view);
                            return true;
                        }
                        return false;
                    }
                });
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
    private TextView userEntryBox, userTextBox;

    // It is called when the keyboard appears
    @Override
    public View onCreateInputView() {
        Log.e("___Galaxy Keyboard___", "CREATED");
        View galaxyView = getLayoutInflater().inflate(R.layout.galaxy_keyboard, null);
        predictListView = galaxyView.findViewById(R.id.predictListView);
        GalaxyFbLayoutManager layoutManager = new GalaxyFbLayoutManager(context);
//        layoutManager.setFlexDirection(FlexDirection.ROW_REVERSE);
        layoutManager.setFlexWrap(FlexWrap.WRAP_REVERSE);
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        predictListView.setLayoutManager(layoutManager);

        userEntryBox = galaxyView.findViewById(R.id.userEntryBox);
        userTextBox = galaxyView.findViewById(R.id.userTextBox);
        kv = galaxyView.findViewById(R.id.keyboardView);
        kv.setInputConnection(ic);
        keyboard = new Keyboard(this, R.xml.bur_front_keypad);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return galaxyView;
    }

}
