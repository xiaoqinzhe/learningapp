package com.example.learningapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.widget.EmojiTextView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.learningapp.R;
import com.example.learningapp.utils.Common;
import com.example.learningapp.utils.L;
import com.example.learningapp.views.emoji.EmojiTransferManager;

public class EmojiActivity extends AppCompatActivity {

    private TextView emojiTv;
    private EmojiTextView emojiEtv;
    private EditText editText;
    private TextView unicodeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);

        emojiTv = findViewById(R.id.views_emoji_tv);
        emojiEtv = findViewById(R.id.views_emoji_etv);
        editText = findViewById(R.id.views_emoji_edittext);
        unicodeTv = findViewById(R.id.views_emoji_unicode_tv);

//        String unicodeStr = "\\uD83D\\uDE01";
        String unicodeStr = "\\ud83d\\ude02";
//
//        L.d(unicodeStr);
//
        String unicode = Common.stringToUnicode(unicodeStr);
//        L.d(unicode);
//
//        L.d(Common.unicodeToString((unicode)));

//        String unicode = new String(Character.toChars(Integer.parseInt("1f49e", 16)));

        useTTF();
//        emojiTv.setText(unicode);

        emojiTv.setText(EmojiCompat.get().process(unicode));

        emojiEtv.setText(unicode);
    }

    public void useTTF(){
        Typeface tf = Typeface.createFromAsset(getAssets(), "NotoColorEmojiCompat.ttf");
        if (tf != null)
            L.d(""+tf.toString());
        emojiTv.setTypeface(tf);
    }

    public void showUnicode(View view) {
        String text = editText.getText().toString();
        L.d(text);
        String str = Common.unicodeToString(text);
        unicodeTv.setText(str);
    }

    public void transferEmoji(View view) {
        String text = editText.getText().toString();
        L.d(text);
        unicodeTv.setText( EmojiTransferManager.parse(text, unicodeTv.getTextSize()) );
    }

    //text:原本字符串

}
