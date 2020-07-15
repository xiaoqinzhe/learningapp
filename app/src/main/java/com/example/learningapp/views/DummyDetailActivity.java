package com.example.learningapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.example.learningapp.R;
import com.example.learningapp.fragments.DetailsFragment;

public class DummyDetailActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener {
    public static final String DUMMY_ARG_KEY = "dummy_arg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_detail);
        Bundle detailArgs = getIntent().getBundleExtra(DUMMY_ARG_KEY);
        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(detailArgs);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_dummy_detail_activity, detailsFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
