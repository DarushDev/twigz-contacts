package com.example.twigzcontacts.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.twigzcontacts.R;
import com.example.twigzcontacts.webservice.TwilioClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewMessageActivity extends AppCompatActivity {

    EditText field_message;
    Button button_send;
    String message;
    String phoneNumber;
    public static final String ACCOUNT_SID = "AC086325fcd8ae251b9c22b6ee6e993c18";
    public static final String AUTH_TOKEN = "e1a61c19e3aac553d2b0dcc79681134e";
    public static final String BASE_URL = "https://api.twilio.com";
    public static final String TAG = "mytag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        message = "Hi. Your OTP is: " + getRandomOtp();

        if (getIntent().hasExtra("phoneNumber")) {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
        }

        field_message = (EditText) findViewById(R.id.field_newmessage_message);
        field_message.setText(message);

        button_send = (Button) findViewById(R.id.button_newmessage_send);

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage(message, phoneNumber);
                //Toast.makeText(NewMessageActivity.this, "Sending...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public int getRandomOtp() {
        Random random = new Random();
        int num = 100000 + random.nextInt(900000);
        return num;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void sendMessage(String message, String phoneNumber) {

        String body = message;
        String to = phoneNumber;
        String from = "+14243533758";

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );

        Map<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Body", body);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TwilioClient twilioClient = retrofit.create(TwilioClient.class);
        Call call = twilioClient.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data);
        call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.isSuccessful()) {
                            Log.d(TAG, "onResponse: Successful");
                            try {
                                Log.d(TAG, response.toString());
                                Log.d(TAG, response.body().string());
                                Log.d(TAG, response.body().contentType().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "onResponse: Failed");
                            Log.d(TAG, response.toString());
                            Log.d(TAG, ">>>>>>>>>>>>>>>");
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());

                    }
                });

    }

}
