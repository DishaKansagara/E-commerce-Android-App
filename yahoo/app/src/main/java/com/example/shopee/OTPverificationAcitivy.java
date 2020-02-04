package com.example.shopee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

public class OTPverificationAcitivy extends AppCompatActivity {
    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification_acitivy);

        phoneNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn =findViewById(R.id.verify_btn);
        userNo = getIntent().getStringExtra("mobileNo");
        phoneNo.setText("Verification code has been sent to +91 " +userNo);

        Random random = new Random();
        final int OTP_number = random.nextInt(999999 - 111111) +111111;
        String SMS_API = "https://www.fast2sms.com/dev/bulk";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,SMS_API , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (otp.getText().toString().equals(String.valueOf(OTP_number))){

                            Map<String, Object> updateStatus = new HashMap<>();
                            updateStatus.put("Payment_Status", "Paid");
                            updateStatus.put("Order_Status", "Ordered");
                            String orderID = getIntent().getStringExtra("OrderID");
                            FirebaseFirestore.getInstance().collection("ORDER").document(orderID)
                                    .update(updateStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        DeliveryActivity.codOrderConfirmed = true;
                                        finish();
                                    }else{
                                        Toast.makeText(OTPverificationAcitivy.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(OTPverificationAcitivy.this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(OTPverificationAcitivy.this, "failed to send the OTP verification code !", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               Map<String,String> headers = new HashMap<>();
               headers.put("authorization","ZwAB5UduM30Ecg4Is7r8CmXfxS16aTpyKkhtqjR9FYVHnLoPOzYzacPAWZwiK3SB1Qf9Ogrxbkotmps2");
               return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> body = new HashMap<>();
                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers",userNo);
                body.put("message","6436");
                body.put("variables","{#BB#}");
                body.put("variables_values",String.valueOf(OTP_number));



                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
               5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(OTPverificationAcitivy.this);
        requestQueue.add(stringRequest);
    }
}
