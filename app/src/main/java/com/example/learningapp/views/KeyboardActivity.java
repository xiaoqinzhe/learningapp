package com.example.learningapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.learningapp.R;
import com.example.learningapp.utils.L;

public class KeyboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        View container = findViewById(R.id.keyboard_ac_container);
        View moveView = findViewById(R.id.btn_keyboard_login);

//        adjustKeyboard(container, moveView);
    }

    private void adjustKeyboard(final View container, final View moveView) {
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                L.d("onGlobalLayout");
                int[] location = new int[2];
                Rect rect = new Rect();
                container.getWindowVisibleDisplayFrame(rect);
                container.getLocationInWindow(location);
                int container_bottom = location[1] + container.getHeight();
//                int container_bottom = container.getRootView().getHeight();
                int invisibleHeight = container_bottom - rect.bottom;

                L.d("invisibleHeight "+invisibleHeight+" "+ container_bottom +" "+rect.bottom);
                if (invisibleHeight > 500){

                    moveView.getLocationInWindow(location);
                    int scrollHeight = location[1] + moveView.getHeight() - rect.bottom;
                    L.d("location "+location[0]+" "+location[1]);
                    L.d("scrollHeight "+scrollHeight);

                    container.scrollTo(0, container.getScrollY()+scrollHeight);
//                    container.scrollBy();
                }else{
                    container.scrollTo(0,0);
                }
            }
        });
    }
}
