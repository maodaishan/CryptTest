package com.maods.crypttest;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

public class CryptActivity extends Activity {
    private static final String RSA = "RSA";
    private static final String CHINESE_STD = "国密";
    private static final int ALG_RSA =1;
    private static final int ALG_CHINESE_STD = 2;
    private static final String[] ALGS=new String[]{
            RSA,
            CHINESE_STD
    };
    private Button mChooseAlg;
    private TextView mCurrAlgView;
    private TextView mCurrKeys;
    private EditText mInput;
    private Button mEncrypt;
    private TextView mEncryptResult;
    private Button mDecrypt;
    private TextView mDecryptResult;
    private KeyPair mKeyPair;
    private int mCurrAlg = ALG_RSA;
    private byte[] mEncryptedData;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crypt);
        mChooseAlg=findViewById(R.id.choose_alg);
        mChooseAlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseAlgClicked();
            }
        });
        mCurrAlgView=findViewById(R.id.curr_alg);
        mCurrKeys=findViewById(R.id.curr_keys);
        mInput=findViewById(R.id.input);
        mEncrypt=findViewById(R.id.encrypt);
        mEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEncryptClicked();
            }
        });
        mEncryptResult=findViewById(R.id.encrypt_result);
        mDecrypt=findViewById(R.id.decrypt);
        mDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDecryptClicked();
            }
        });
        mDecryptResult=findViewById(R.id.decrypt_result);
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshKeys();
        refreshCurrAlg();
    }

    private void refreshKeys(){
        mKeyPair = CryptOperations.getKeyPair("RSA");
        StringBuilder sb = new StringBuilder();
        sb.append("Current private key:"+mKeyPair.getPrivate().toString()+"\n");
        sb.append("Current public key:"+mKeyPair.getPublic().toString());
        mCurrKeys.setText(sb.toString());
    }
    private void refreshCurrAlg(){
        String alg=null;
        switch(mCurrAlg){
            case ALG_RSA:
                alg=RSA;
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
                            mCurrAlg=ALG_RSA;
                            Toast.makeText(CryptActivity.this,"当前不支持国密",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case RSA:
                    default:
                        mCurrAlg=ALG_RSA;
                        break;
                }
                dialog.dismiss();
                refreshCurrAlg();
            }
        });
    }

    private void onEncryptClicked(){
        String sec=mInput.getText().toString();
        if(TextUtils.isEmpty(sec)){
            mEncryptResult.setText("输入为空");
            return;
        }
        mEncryptedData=CryptOperations.encrypt(sec.getBytes(),mKeyPair.getPublic());
        if(mEncryptedData==null){
            mEncryptResult.setText("加密出错");
        }else{
            mEncryptResult.setText(mEncryptedData.toString());
        }
    }

    private void onDecryptClicked(){
        if(mEncryptedData.length==0){
            Toast.makeText(this,"待解密内容为空",Toast.LENGTH_LONG).show();
            return;
        }
        byte[] decrypted = CryptOperations.decrypt(mEncryptedData,mKeyPair.getPrivate());
        mDecryptResult.setText(new String(decrypted));
    }
}
