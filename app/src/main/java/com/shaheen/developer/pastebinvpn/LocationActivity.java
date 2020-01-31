package com.shaheen.developer.pastebinvpn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.atom.core.exceptions.AtomException;
import com.atom.core.models.Country;
import com.atom.sdk.android.data.callbacks.CollectionCallback;
import com.shaheen.developer.pastebinvpn.Adapter.LocationListAdapter;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {


    private SearchView searchView;
    private RecyclerView recyclerView;
    private LocationListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Country> arrayList = new ArrayList<>();
    ProgressBar progress_circular;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.location));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progress_circular = (ProgressBar) findViewById(R.id.progress_circular);
        progress_circular.setVisibility(View.VISIBLE);
        searchView = (SearchView)findViewById(R.id.searchView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        if (AtomDemoApplicationController.getInstance().getAtomManager() !=null){
            Log.d("shani","atom not null ....... ");
        }else {
            Log.d("shani","atom is null ....... ");
        }

        AtomDemoApplicationController.getInstance().getAtomManager().getCountries(new CollectionCallback<Country>() {
            @Override
            public void onSuccess(List<Country> list) {
                Log.d("shani","success list "+list.size());
                arrayList.addAll(list);
                SetAdapter();
            }

            @Override
            public void onError(AtomException e) {
                Log.d("shani","AtomException onError "+e.getMessage());
                Log.d("shani","AtomException onError "+e.getErrorMessage());
                Log.d("shani","AtomException onError "+e.getCode());
                Log.d("shani","AtomException onError "+e.getApiError());
            }

            @Override
            public void onNetworkError(AtomException e) {
                Log.d("shani","AtomException onNetworkError "+e.getMessage());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length()<1){
                    SetAdapter();
                }else {
                    ApplyFilters(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (s.length()<1){
                    SetAdapter();
                }else {
                    ApplyFilters(s);
                }
                return false;
            }
        });
        
    }



    public void RefreshLocation(){

        Intent data = new Intent();
        String text = "country";
        data.setData(Uri.parse(text));
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d("shani","called....");
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SetAdapter(){

        progress_circular.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(LocationActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LocationListAdapter(arrayList, LocationActivity.this,LocationActivity.this);
        recyclerView.setAdapter(mAdapter);
        Log.d("shani","adapter end "+arrayList.size());
    }

    private void ApplyFilters(String string){

        ArrayList<Country> searchTemList = new ArrayList<>();
        for (int i = 0; i <arrayList.size() ; i++) {
            String appName_lower = arrayList.get(i).getName().toLowerCase();
            String string_lower = string.toLowerCase();
            if (appName_lower.contains(string_lower)){
                searchTemList.add(arrayList.get(i));
            }
        }

        mLayoutManager = new LinearLayoutManager(LocationActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LocationListAdapter(searchTemList, LocationActivity.this,LocationActivity.this);
        recyclerView.setAdapter(mAdapter);
    }






}
