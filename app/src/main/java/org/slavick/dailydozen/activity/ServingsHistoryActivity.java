package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.ServingsHistoryAdapter;

public class ServingsHistoryActivity extends AppCompatActivity {
    private RecyclerView servingsHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);

        servingsHistory = (RecyclerView) findViewById(R.id.servings_history);

        servingsHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        servingsHistory.setAdapter(new ServingsHistoryAdapter());
    }
}
