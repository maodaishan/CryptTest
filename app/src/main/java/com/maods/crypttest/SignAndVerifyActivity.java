package com.maods.crypttest;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignAndVerifyActivity extends Activity {
    private static final String ECDSA = "SHA256withECDSA";
    private static final String CHINESE_STD = "国密";
    private static final int ALG_ECDSA =1;
    private static final int ALG_CHINESE_STD = 2;
    private static final String[] ALGS=new String[]{
            ECDSA,
            CHINESE_STD
    };

    private TextView mCurrKeys;
    private Button mChooseAlg;
    private TextView mCurrAlgView;
    private EditText mInput;
    private Button mSign;
    private TextView mSignResult;
    private Button mModify;
    private Button mVerify;
    private TextView mVerifyResult;
    private KeyPair mKeyPair;
    private int mCurrAlg = ALG_ECDSA;
    private byte[] mSignedData;
    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.sign_and_verify);
        mCurrKeys=findViewById(R.id.curr_keys);
        mChooseAlg=findViewById(R.id.choose_alg);
        mChooseAlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseAlgClicked();
            }
        });
        mCurrAlgView=findViewById(R.id.curr_alg);
        mInput=findViewById(R.id.input);
        mSign=findViewById(R.id.sign);
        mSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignClicked();
            }
        });
        mSignResult=findViewById(R.id.sign_result);
        mModify=findViewById(R.id.modify);
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onModifyClicked();
            }
        });
        mVerify=findViewById(R.id.verify);
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVerifyClicked();
            }
        });
        mVerifyResult=findViewById(R.id.verify_result);
    }
    protected void onResume(){
        super.onResume();
        refreshKeys();
        refreshCurrAlg();
    }
    private void refreshKeys(){
        mKeyPair = CryptOperations.getKeyPair("EC");
        StringBuilder sb = new StringBuilder();
        sb.append("Current private key:"+mKeyPair.getPrivate().toString()+"\n");
        sb.append("Current public key:"+mKeyPair.getPublic().toString());
        mCurrKeys.setText(sb.toString());
    }
    private void refreshCurrAlg(){
        String alg=null;
        switch(mCurrAlg){
            case ALG_ECDSA:
                alg=ECDSA;
                break;
            case ALG_CHINESE_STD:
                alg=CHINESE_STD;
                break;
        }
        mCurrAlgView.setText("当前采用的算法:"+alg);
    }
    private void onChooseAlgClicked(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("选择ECC还是国密");
        View view=View.inflate(this,R.layout.simplelist,null);
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.show();
        ListView list=view.findViewById(R.id.list);
        List<Map<String,String>> algsArray=new ArrayList<Map<String,String>>();
        for(int i=0;i<ALGS.length;i++){
            String alg=ALGS[i];
            Map<String,String>map=new HashMap<String,String>();
            map.put("name",alg);
            algsArray.add(map);
        }
        SimpleAdapter adapter=new SimpleAdapter(this,algsArray,android.R.layout.simple_list_item_1,new String[]{"name"},new int[]{android.R.id.text1});
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String listItem=ALGS[i];
                switch(listItem){
                    case CHINESE_STD:
                        mCurrAlg=ALG_CHINESE_STD;
                        if(!CryptOperations.IS_SUPPORT_CHINESE_STD){
                            mCurrAlg=ALG_ECDSA;
                            Toast.makeText(SignAndVerifyActivity.this,"当前不支持国密",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case ECDSA:
                    default:
                        mCurrAlg=ALG_ECDSA;
                        break;
                }
                dialog.dismiss();
                refreshCurrAlg();
            }
        });
    }
    private void onSignClicked(){
        String src=mInput.getText().toString();
        try {
            mSignedData = CryptOperations.signData(ECDSA,src.getBytes(),mKeyPair.getPrivate());
        } catch (Exception e) {
            e.printStackTrace();
            mSignResult.setText("签名出错:"+e.toString());
        }
        mSignResult.setText(mSignedData.toString());
    }
    private void onModifyClicked(){
        int len=mSignedData.length;
        mSignedData[len/2]=1;
    }
    private void onVerifyClicked(){
        if(mSignedData==null || mSignedData.length==0){
            mVerifyResult.setText("无数据可验证");
            return;
        }
        boolean verify= false;
        try {
            verify = CryptOperations.verifySign(ECDSA,mInput.getText().toString().getBytes(),mKeyPair.getPublic(),mSignedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVerifyResult.setText("验证结果："+verify);
    }
}
