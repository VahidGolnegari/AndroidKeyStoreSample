package com.golnegari.keystoreexample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.golnegari.keystoreexample.encryption.MyEncryption;

public class RsaActivity extends AppCompatActivity {

    private final String MESSAGE = "Hello World";
    private MyEncryption myEncryption;
    private String encryptedMesage ;

    private TextView txtMessage;
    private TextView txtDecrptedMessage ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keystore);
        txtMessage = findViewById(R.id.textview_keystore_message);
        txtDecrptedMessage = findViewById(R.id.textview_keystore_decryptedmessage);
        myEncryption = new MyEncryption(this);

    }

    public void encrypt(View view) {
         encryptedMesage = myEncryption.encrypt(MESSAGE);
         txtMessage.setText(encryptedMesage);
    }

    public void decrypt(View view) {
        String decryptedMessage = myEncryption.decrypt(encryptedMesage);
        txtDecrptedMessage.setText(decryptedMessage);
    }


}
