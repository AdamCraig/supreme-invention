package com.epicodus.CritterSavior.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.CritterSavior.R;
import com.epicodus.CritterSavior.adapters.PetListAdapter;
import com.epicodus.CritterSavior.models.Pet;
import com.epicodus.CritterSavior.services.PetService;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchResultsActivity extends AppCompatActivity {

    @Bind(R.id.petsListRecyclerView) RecyclerView mPetsListRecyclerView;
    @Bind(R.id.noResultsImageView) ImageView mNoResultsImageView;
    @Bind(R.id.noResultsTextView) TextView mNoResultsTextView;

    private PetListAdapter mAdapter;

    public ArrayList<Pet> mPets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String location = intent.getStringExtra("location");
        String species = intent.getStringExtra("species");
        String size = intent.getStringExtra("size");
        String breed = intent.getStringExtra("breed");
        String sex = intent.getStringExtra("sex");
        String age = intent.getStringExtra("age");

        getPetsByLocation(location, species, size, breed, sex, age);
    }

    private void getPetsByLocation(String location, String species, String size, String breed, String sex, String age) {
        final PetService petService = new PetService();
        petService.findPetsByLocation(location, species, size, breed, sex, age, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mPets = petService.processResults(response);

                SearchResultsActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter = new PetListAdapter(getApplicationContext(), mPets);
                        mPetsListRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchResultsActivity.this);
                        mPetsListRecyclerView.setLayoutManager(layoutManager);
                        mPetsListRecyclerView.setHasFixedSize(true);

                        if (mPets.isEmpty()) {
                            mNoResultsImageView.setVisibility(View.VISIBLE);
                            mNoResultsTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
    }
}
