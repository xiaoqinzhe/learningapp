package com.example.learningapp.fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.learningapp.views.DummyDetailActivity;
import com.example.learningapp.R;
import com.example.learningapp.fragments.dummy.DummyContent;

public class DummyActivity extends AppCompatActivity
        implements ItemFragment.OnListFragmentInteractionListener,
        DetailsFragment.OnFragmentInteractionListener {
    Boolean dualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        dualPane = findViewById(R.id.fragment_dummy_details) != null
                && findViewById(R.id.fragment_dummy_details).getVisibility() == View.VISIBLE;
        Log.i("DummyActivity", "here");
//        ItemFragment dummyListFragment = new ItemFragment();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_dummy_list, dummyListFragment)
//                .commit();
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.i("DummyActivity", "item click "+item.id);
        Bundle args = new Bundle();
        args.putString(DetailsFragment.ARG_PARAM1, item.id);
        args.putString(DetailsFragment.ARG_PARAM2, item.content);
        if (dualPane){
            DetailsFragment details = new DetailsFragment();
            details.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack("hhh")
                    .replace(R.id.fragment_dummy_details, details)
                    .commit();
        }else{
            Intent intent = new Intent(this, DummyDetailActivity.class);
            intent.putExtra(DummyDetailActivity.DUMMY_ARG_KEY, args);
            startActivity(intent);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
