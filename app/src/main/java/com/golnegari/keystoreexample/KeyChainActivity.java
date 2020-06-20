package com.golnegari.keystoreexample;

import android.content.Intent;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.X500NameBuilder;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v1CertificateBuilder;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class KeyChainActivity extends AppCompatActivity implements KeyChainAliasCallback {

    private boolean isCertCreated = false;
    X509CertificateHolder x509CertificateHolder;
    private final String TAG = "KeyChain";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keychain);
        findViewById(R.id.button_keychain_create).setEnabled(!isCertCreated);

    }

    public void createCert(View view) {
        if (!isCertCreated) createCert();
    }

    public void importCert(View view) {
        KeyChain.choosePrivateKeyAlias(this, this,
                new String[] { "RSA" }, null, null, -1, null);
    }

    public void exportCert(View view) {
        Intent intent = KeyChain.createInstallIntent();
        byte[] p12 = new byte[0];
        try {
            p12 = x509CertificateHolder.getEncoded();
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, x509CertificateHolder.getEncoded());
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createCert(){
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (kpg != null) {
            kpg.initialize(1024);
            KeyPair keyPair = kpg.genKeyPair();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.YEAR , 20);

            X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            nameBuilder.addRDN(BCStyle.CN , "GAP");
            nameBuilder.addRDN(BCStyle.C , "jp");

            X500Name x500Name = nameBuilder.build();
            Random random = new Random();

            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
            X509v1CertificateBuilder v1CertGen = new X509v1CertificateBuilder(x500Name
                    , BigInteger.valueOf(random.nextLong())
                    ,startDate.getTime()
                    ,endDate.getTime()
                    ,x500Name
                    ,subjectPublicKeyInfo);

            // Prepare Signature:
            ContentSigner sigGen = null;
            try {
                Security.addProvider(new BouncyCastleProvider());
                sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("SC").build(keyPair.getPrivate());
            } catch (OperatorCreationException e) {
                e.printStackTrace();
            }
            // Self sign :
            x509CertificateHolder = v1CertGen.build(sigGen);
            try {
                isCertCreated = x509CertificateHolder.getEncoded().length > 0 ;
                if (isCertCreated) {
                    Toast.makeText(this , "Cert has Been Created Successfully" , Toast.LENGTH_LONG).show();
                    Log.d(TAG , "Cert Bytes : " + Arrays.toString(x509CertificateHolder.getEncoded()));
                } else Toast.makeText(this , "Fail to create Cert" , Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                isCertCreated = false;
            }
        }
    }


    @Override
    public void alias(@Nullable String s) {
        Log.d(TAG , "alias : " + s);

    }
}
