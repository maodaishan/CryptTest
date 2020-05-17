package com.maods.crypttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private static final String[] ACTIONS=new String[]{
            CryptOperations.MANAGE_KEYS,
            CryptOperations.CRYPT_DECRYPT,
            CryptOperations.SIGN_AND_VERIFY
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView=findViewById(R.id.list);
        mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ACTIONS);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                handleClicked(i);
            }
        });
    }

    private void handleClicked(int i){
        Intent intent = new Intent();
        switch(ACTIONS[i]){
            case CryptOperations.MANAGE_KEYS:
                intent.setClass(this,ManageKeysActivity.class);
                startActivity(intent);
                break;
            case CryptOperations.CRYPT_DECRYPT:
                intent.setClass(this,CryptActivity.class);
                startActivity(intent);
                break;
            case CryptOperations.SIGN_AND_VERIFY:
                intent.setClass(this,SignAndVerifyActivity.class);
                startActivity(intent);
                break;
        }
    }
}
