package com.example.shopee;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.example.shopee.DeliveryActivity.SELECT_ADDRESS;
import static com.example.shopee.MyAccountFragment.MANAGE_ADDRESS;
import static com.example.shopee.MyAddressesActivity.refreshItem;

public class AddressAdapater extends RecyclerView.Adapter<AddressAdapater.ViewHolder> {

    private List<AddressesModel> addressesModelList;
    private  int MODE;
    private int  preselectedposition;

    public AddressAdapater(List<AddressesModel> addressesModelList, int MODE) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preselectedposition = DBqueries.selectedAddress;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addresses_item_layout,viewGroup,false);
        return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapater.ViewHolder viewholder, int position) {

        String name = addressesModelList.get(position).getFullname();
        String mobileNo = addressesModelList.get(position).getMobileNo();
        String address = addressesModelList.get(position).getAddress();
        String pincode = addressesModelList.get(position).getPincode();
        Boolean selected = addressesModelList.get(position).getSelected();

        viewholder.setData(name,address,pincode,selected,position,mobileNo);


    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        private TextView fullname;
        private TextView address;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout optionContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pincode);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private  void setData(String username, String useraddress, String userpincode, final Boolean selected, final int position, String mobileNo){
            fullname.setText(username+" - "+mobileNo);
            address.setText(useraddress);
            pincode.setText(userpincode);


            if (MODE == SELECT_ADDRESS){
                icon.setImageResource(R.drawable.checkmark);
                if (selected){
                    icon.setVisibility(View.VISIBLE);
                    preselectedposition = position;
                }else{
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(preselectedposition != position){
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preselectedposition).setSelected(false);
                            refreshItem(preselectedposition,position);
                            preselectedposition = position;
                            DBqueries.selectedAddress = position;
                        }


                    }
                });
            }else if (MODE == MANAGE_ADDRESS);{
                optionContainer.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.hamburger_button);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionContainer.setVisibility(View.VISIBLE);
                        refreshItem(preselectedposition,preselectedposition);
                        preselectedposition = position;
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(preselectedposition,preselectedposition);
                        preselectedposition = -1;
                    }
                });

            }
        }
    }
}
