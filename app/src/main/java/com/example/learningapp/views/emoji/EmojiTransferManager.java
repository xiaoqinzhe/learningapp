package com.example.learningapp.views.emoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.example.learningapp.R;
import com.example.learningapp.utils.L;
import com.example.learningapp.views.text.CenterImageSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiTransferManager {
    public final static char MY_EMOJI_DELIMITER_START = '[';
    public final static char MY_EMOJI_DELIMITER_END = ']';

    private static Context globalContext;

    private static Map<Integer, Drawable> emojiCodeMap;
    private static List<String> myEmojiStringList;
    private static Map<String, Drawable> myEmojiMap;

    public static void init(Context context){
        globalContext = context.getApplicationContext();

        initEmojiCodeMap();

        initMyEmojiMap();

    }

    private static void initMyEmojiMap() {
        myEmojiStringList = new ArrayList<>();
        myEmojiMap = new HashMap<>();
        String[] codes = globalContext.getResources().getStringArray(R.array.myemoji_transfer_code);
        TypedArray typedArray = globalContext.getResources().obtainTypedArray(R.array.myemoji_transfer_drawable);
        if (codes.length != typedArray.length())
            throw new AssertionError();
        for (int i=0; i<codes.length; ++i){
            myEmojiStringList.add(codes[i]);
            myEmojiMap.put(codes[i], typedArray.getDrawable(i));
        }
    }

    private static void initEmojiCodeMap(){
        emojiCodeMap = new HashMap<>();
        int[] codes = globalContext.getResources().getIntArray(R.array.emoji_transfer_code);
        TypedArray typedArray = globalContext.getResources().obtainTypedArray(R.array.emoji_transfer_drawable);
        if (codes.length != typedArray.length())
            throw new AssertionError();
        for (int i=0; i<codes.length; ++i)
            emojiCodeMap.put(codes[i], typedArray.getDrawable(i));
    }

    public static CharSequence parse(String text, float textSize) {
        if (text == null) {
            return "";
        }
        final char[] chars = text.toCharArray();
        final SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        transferEmoji(chars, textSize, ssb);

        transferMyEmoji(chars, textSize, ssb);

        return ssb;
    }

    private static void transferMyEmoji(char[] chars, float textSize, SpannableStringBuilder ssb) {
        boolean flag = false;
        boolean matched = false;
        int start = 0, end = 0;
        for (int i=0; i<chars.length; ++i){
            if (chars[i] == MY_EMOJI_DELIMITER_START){
                start = i;
                flag = true;
            }else if (flag && chars[i] == MY_EMOJI_DELIMITER_END){
                end = i;
                flag = false;
                matched = true;
            }
            if (matched){
                if (end - start > 1) {
                    String str = new String(chars, start+1, end - start - 1);
                    L.d(""+start+" "+end+" "+str);
                    if (myEmojiMap.containsKey(str)) {
                        Drawable drawable = myEmojiMap.get(str);
                        drawable.setBounds(0, 0, (int) textSize, (int) textSize);
                        ImageSpan imageSpan = new ImageSpan(drawable, CenterImageSpan.ALIGN_CENTER);
                        ssb.setSpan(imageSpan, start, end+1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                matched = false;
            }
        }
    }

    private static void transferEmoji(char[] chars, float textSize, SpannableStringBuilder ssb) {
        int codePoint;
        boolean isSurrogatePair;
        for (int i = 0; i < chars.length; i++) {
            if (Character.isHighSurrogate(chars[i])) {
                continue;
            } else if (Character.isLowSurrogate(chars[i])) {
                if (i > 0 && Character.isSurrogatePair(chars[i - 1], chars[i])) {
                    codePoint = Character.toCodePoint(chars[i - 1], chars[i]);
                    isSurrogatePair = true;
                } else {
                    continue;
                }
            } else {
                codePoint = (int) chars[i];
                isSurrogatePair = false;
            }

            if (emojiCodeMap.containsKey(codePoint)) {
//                Bitmap bitmap = BitmapFactory.decodeResource(globalContext.getResources(), emojiCodeMap.get(codePoint));
//                BitmapDrawable drawable = new BitmapDrawable(globalContext.getResources(), bitmap);
                Drawable drawable = emojiCodeMap.get(codePoint);
                drawable.setBounds(0, 0, (int) textSize, (int) textSize);
//                CenterImageSpan imageSpan = new CenterImageSpan(bmpDrawable);
                CenterImageSpan imageSpan = new CenterImageSpan(drawable, CenterImageSpan.ALIGN_CENTER);
                ssb.setSpan(imageSpan, isSurrogatePair ? i - 1 : i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public static List<String> getMyEmojiStringList() {
        return myEmojiStringList;
    }

    public static Map<String, Drawable> getMyEmojiMap() {
        return myEmojiMap;
    }

    public static Drawable getMyEmojiDrawable(String code){
        if (myEmojiMap.containsKey(code)){
            return myEmojiMap.get(code);
        }
        return null;
    }

    public static String encodeEmojiCode(String code){
        return MY_EMOJI_DELIMITER_START + code + MY_EMOJI_DELIMITER_END;
    }
}
