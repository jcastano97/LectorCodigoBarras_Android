package com.example.juanz.lectorcodigodebarrascedula;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button scanBtn;
    private TextView formatTxt, contentTxt, cedulaTxt;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);
        cedulaTxt = (TextView) findViewById(R.id.scan_cedula);
        scanBtn.setOnClickListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onClick(View v) {
        //respond to clicks
        if (v.getId() == R.id.scan_button) {
            //scan
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = (String) scanningResult.getContents();
            String scanFormat = (String) scanningResult.getFormatName();
            formatTxt.setText("Formato: " + scanFormat);
            contentTxt.setText("Contenido : " + scanContent);

            if (scanContent.indexOf("PubDSK_") != -1) {
                int contador = 0;
                for (int i = 0; i < scanContent.indexOf("PubDSK_"); i++) {
                    if (Character.isDigit(scanContent.charAt(i)) || Character.isAlphabetic(scanContent.charAt(i))) {
                        if (contador == 0 && (scanContent.charAt(i) == 'I' || scanContent.charAt(i) == 'i')){
                            i = scanContent.indexOf("PubDSK_");
                            contador = 0;
                        }else {
                            contador = contador + 1;
                        }
                    }
                }

                String[] Filtro1 = scanContent.split("PubDSK_");
                scanContent = Filtro1[1];
                String[] Filtro2 = scanContent.split("[a-zA-Z]");
                scanContent = Filtro2[0];
                if (scanFormat.equals("PDF_417")) {

                    if (contador == 10) {
                        cedulaTxt.setText("Cedula: " + scanContent.substring(15, scanContent.length()));
                    } else {
                        if (contador == 9) {
                            cedulaTxt.setText("Cedula: " + scanContent.substring(19, scanContent.length()));
                        } else {
                            if (contador == 0){
                                cedulaTxt.setText("Cedula: " + scanContent.substring(17, scanContent.length()));
                            }else{
                                cedulaTxt.setText("Cedula" + Integer.toString(contador) + ": No se pudo detectar Correctamente. " + scanContent);
                            }
                        }
                    }
                } else {
                    cedulaTxt.setText("");
                }
            }else{
                cedulaTxt.setText("Lo sentimos pero el formato PDF_417 no pertenece a una cedula de ciudadania colombiana");
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}