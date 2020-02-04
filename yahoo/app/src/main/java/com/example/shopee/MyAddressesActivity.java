package com.example.shopee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.shopee.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressesActivity extends AppCompatActivity {
    private int previousAddress;
    private LinearLayout addNewAddressBtn;
    private RecyclerView myaddressesRecyclerView;
    private static AddressAdapater addressAdapater;
    private Button deliverhereBtn;
    private TextView addressesSaved;
    private  Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        previousAddress = DBqueries.selectedAddress;

        myaddressesRecyclerView = findViewById(R.id.addresses_recyclerView);
        //deliverhereBtn = findViewById(R.id.view_all_addresses_button);
        deliverhereBtn = findViewById(R.id.deliver_here_Btn);
        addNewAddressBtn = findViewById(R.id.add_new_addressBtn);
        addressesSaved = findViewById(R.id.address_saved);


        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        LayoutManager.setOrientation(RecyclerView.VERTICAL);
        myaddressesRecyclerView.setLayoutManager(LayoutManager);


        int mode = getIntent().getIntExtra("MODE",-1);
        if (mode == SELECT_ADDRESS){
        //   deliverhereBtn.setVisibility (View.VISIBLE); TODO: Error here for delivery button vibility and chekmark on addresses.
        }else {
//           deliverhereBtn.setVisibility(View.GONE);
        }
        deliverhereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if (DBqueries.selectedAddress !=previousAddress){
                 final int previousAddressIndex = previousAddress;
                 loadingDialog.show();


                 Map<String,Object> updateSelection = new HashMap<>();
                 updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                 updateSelection.put("selected_"+String.valueOf(DBqueries.selectedAddress+1),true);

                 previousAddress = DBqueries.selectedAddress;

                 FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                         .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()){
                          finish();
                         }else{
                             previousAddress = previousAddressIndex;
                             String error= task.getException().getMessage();
                             Toast.makeText(MyAddressesActivity.this, "error", Toast.LENGTH_SHORT).show();
                         }
                         loadingDialog.dismiss();
                     }
                 });
             }else {
                 finish();
             }
            }
        });
        addressAdapater = new AddressAdapater(DBqueries.addressesModelList,mode);
        myaddressesRecyclerView.setAdapter(addressAdapater);
        ((SimpleItemAnimator)myaddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressAdapater.notifyDataSetChanged();

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAddressIntent = new Intent(MyAddressesActivity.this, AddAddressActivity.class);
               addAddressIntent.putExtra("INTENT", "null");
                startActivity(addAddressIntent);
            }
        });
        addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+" saved addresses");

    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+" saved addresses");
    }

    public static void refreshItem(int deselect, int select){
        addressAdapater.notifyItemChanged(deselect);
        addressAdapater.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if(DBqueries.selectedAddress != previousAddress){
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                DBqueries.selectedAddress = previousAddress;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void  onBackPressed(){
        if(DBqueries.selectedAddress != previousAddress){
            DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
            DBqueries.addressesModelList.get(previousAddress).setSelected(true);
            DBqueries.selectedAddress = previousAddress;
        }
        super.onBackPressed();
    }
}
