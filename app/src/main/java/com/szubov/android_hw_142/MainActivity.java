package com.szubov.android_hw_142;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final String KEY1 = "key1";
    private final String KEY2 = "key2";
    private SharedPreferences mListSharedPref;
    private final String LIST_TEXT = "list_text";
    private List<Map<String,String>> mArrayList = new ArrayList<>();
    private ArrayList<Integer> mArrayListDeleteIndexes = new ArrayList<>();
    private BaseAdapter mListContentAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private static final String DELETE_INDEXES = "array_list_indexes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        ListView mListView = findViewById(R.id.list);
        mListSharedPref = getSharedPreferences("MyText", MODE_PRIVATE);
        mSwipeRefresh = findViewById(R.id.swipeRefresh);

        if (!mListSharedPref.contains(getString(R.string.large_text))) {
            SharedPreferences.Editor myEditor = mListSharedPref.edit();
            myEditor.putString(LIST_TEXT, getString(R.string.large_text));
            myEditor.apply();
        }

        prepareContent();
        mListContentAdapter = createAdapter(mArrayList);
        mListView.setAdapter(mListContentAdapter);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareContent();
                mListContentAdapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mArrayList.remove(position);
                mArrayListDeleteIndexes.add(position);
                mListContentAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(DELETE_INDEXES, mArrayListDeleteIndexes);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.size() > 0) {
            ArrayList<Integer> list = new ArrayList<>(Objects.requireNonNull(savedInstanceState.
                    getIntegerArrayList(DELETE_INDEXES)));
            for (Integer index : list) {
                mArrayList.remove(index.intValue());
                mArrayListDeleteIndexes.add(index);
            }
            mListContentAdapter.notifyDataSetChanged();
        }
    }

    private BaseAdapter createAdapter(List<Map<String,String>> arrayList) {
        return new SimpleAdapter(this, arrayList, R.layout.list_item,
                new String[]{KEY1,KEY2}, new int[]{R.id.textView1, R.id.textView2});
    }

    private void prepareContent() {
        String[] arrayContent = mListSharedPref.getString(LIST_TEXT,"").split("\n\n");
        mArrayList.clear();
        for (String s : arrayContent) {
            Map<String, String> map = new HashMap<>();
            map.put(KEY1, s);
            map.put(KEY2, s.length() + "");
            mArrayList.add(map);
        }
    }
}