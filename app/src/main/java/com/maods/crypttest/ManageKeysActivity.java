package com.maods.crypttest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class ManageKeysActivity extends Activity {
    private TextView mPrivKey;
    private TextView mPubKey;
    private Button mGenKey;
    private Button mImportKey;
    private KeyPair mKeyPair;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_keys);
        mPrivKey=findViewById(R.id.dft_priv_key);
        mPubKey=findViewById(R.id.dft_pub_key);
        mGenKey=findViewById(R.id.gen_keys);
        mGenKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGenKeyClicked();
            }
        });
        mImportKey=findViewById(R.id.import_key);
        mImportKey.setEnabled(false);
        mImportKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImportKeyClicked();
            }
        });
        refreshKeys();
    }
    protected void onResume(){
        super.onResume();
        refreshKeys();
    }

    private void refreshKeys(){
        if(mKeyPair==null){
            mKeyPair=CryptOperations.getKeyPair("RSA");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Private Key:\n");
        sb.append(mKeyPair.getPrivate().getEncoded().toString());
        /*PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(mKeyPair.getPrivate().getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);*/

        /*ECPrivateKey privKey=(ECPrivateKey)mKeyPair.getPrivate();
        String alg=privKey.getAlgorithm();
        String format=privKey.getFormat();
        byte[] data=privKey.getEncoded();
        sb.append(CryptOperations.decodeBASE64ToStr(data));*/
        mPrivKey.setText(sb.toString());
        sb.setLength(0);
        sb.append("Public Key:\n");
        //sb.append(CryptOperations.decodeBASE64ToStr(mKeyPair.getPublic().getEncoded()));
        sb.append(mKeyPair.getPublic().getEncoded().toString());
        mPubKey.setText(sb.toString());
    }

    private void onGenKeyClicked(){
        mKeyPair=CryptOperations.getKeyPair("RSA");
        refreshKeys();
    }

    private void onImportKeyClicked(){

    }
}
