package com.example.shopee;


import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter {
    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount,boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position){
        switch(cartItemModelList.get(position).getType()){
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout, viewGroup,false);
                return new CartItemViewholder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_total_amount_layout, viewGroup,false);
                return new CartTotalAmountViewholder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (cartItemModelList.get(position).getType()){
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                Long freeCoupens = cartItemModelList.get(position).getFreeCoupens();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIDs = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();

                ((CartItemViewholder)viewHolder).setItemDetails(productID, resource, title, freeCoupens, productPrice, cuttedPrice, offersApplied, position, inStock,String.valueOf(productQuantity),maxQuantity, qtyError, qtyIDs, stockQty);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount = 0;

                for (int x = 0; x < cartItemModelList.size(); x++){
                    if (cartItemModelList.get(x).getType()== CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + quantity;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSeletedCoupenId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice()) * quantity;
                        }else {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())* quantity;
                        }

                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                            savedAmount = savedAmount+(Integer.parseInt(cartItemModelList.get(x).getCuttedPrice())- Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSeletedCoupenId())) {
                                savedAmount = savedAmount+(Integer.parseInt(cartItemModelList.get(x).getProductPrice())- Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;

                                 }
                            }else {
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSeletedCoupenId())) {
                                savedAmount = savedAmount+(Integer.parseInt(cartItemModelList.get(x).getProductPrice())- Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))* quantity;

                            }

                        }
                        }
                    }


                if (totalItemPrice > 200){
                    deliveryPrice = "FREE";
                    totalAmount = totalItemPrice;
                }else {
                    deliveryPrice = "7";
                    totalAmount = totalItemPrice + 7;
                }
                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewholder)viewHolder).setTotalAmount(totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount);

                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount(){
        return cartItemModelList.size();
    }

    class CartItemViewholder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private ImageView freeCoupenIcon;
        private TextView productTitle;
        private TextView freeCoupens;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView offerApplied;
        private TextView coupensApplied;
        private TextView productQuantity;
        private LinearLayout coupenRedemptionLayout;
        private  TextView coupenRedemptionBody;

        private LinearLayout deleteBtn;
        private Button redeemBtn;

        ///coupen dialog
        private  TextView coupenTitle;
        private TextView coupenExpiryDate;
        private TextView coupenBody;
        private RecyclerView coupensRecyclerView;
        private LinearLayout selectedCoupen;
        private TextView originalPrice;
        private TextView discountedPrice;
        private LinearLayout applyORremoveBtnContainer;
        private TextView footerText;
        private Button removeCoupenBtn;
        private Button applyCoupenBtn;
        private String productOriginalPrice;
        ////coupen dialog


        public CartItemViewholder(@NonNull View itemView) {
            super(itemView);
            productImage =itemView.findViewById(R.id.product_image);
            productTitle =itemView.findViewById(R.id.product_title);
            freeCoupenIcon =itemView.findViewById(R.id.free_coupen_icon);
            freeCoupens =itemView.findViewById(R.id.tv_free_coupen);
            productPrice =itemView.findViewById(R.id.product_price);
            cuttedPrice =itemView.findViewById(R.id.cutted_price);
            offerApplied =itemView.findViewById(R.id.offers_applied);
            coupensApplied =itemView.findViewById(R.id.coupons_applied);
            productQuantity =itemView.findViewById(R.id.product_quantity);
            redeemBtn = itemView.findViewById(R.id.coupon_redemption_btn);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
            coupenRedemptionLayout = itemView.findViewById(R.id.coupon_redemption_layout);
            coupenRedemptionBody = itemView.findViewById(R.id.tv_coupon_redemption);
        }
        private void setItemDetails(final String productID, String resource, String title, Long freeCoupensNo, final String productPriceText, String cuttedPriceText, Long offersAppliedNo, final int position , boolean inStock, final String quantity, final Long maxQuantity, boolean qtyError, final List<String> qtyIDs, final long stockQty){

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into(productImage);
            productTitle.setText(title);

            final Dialog checkCoupenpriceDailog = new Dialog(itemView.getContext());
            checkCoupenpriceDailog.setContentView(R.layout.coupen_redeem_dialog);
            checkCoupenpriceDailog.setCancelable(false);
            checkCoupenpriceDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            if(inStock){

                if (freeCoupensNo > 0){
                    freeCoupenIcon.setVisibility(View.VISIBLE);
                    freeCoupens.setVisibility(View.VISIBLE);
                    if(freeCoupensNo == 1){
                        freeCoupens.setText(" free " + freeCoupensNo+ " Coupen ");
                    }else{
                        freeCoupens.setText(" free " + freeCoupensNo+ " Coupens ");
                    }
                }else{
                    freeCoupenIcon.setVisibility(View.INVISIBLE);
                    freeCoupens.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("$"+productPriceText);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("$"+cuttedPriceText);
                coupenRedemptionLayout.setVisibility(View.VISIBLE);

                /////coupen dialog


                ImageView toggleRecyclerView = checkCoupenpriceDailog.findViewById(R.id.toggle_recyclerview);
                coupensRecyclerView = checkCoupenpriceDailog.findViewById(R.id.coupens_recyclerview);
                selectedCoupen = checkCoupenpriceDailog.findViewById(R.id.selected_coupen);
                coupenTitle = checkCoupenpriceDailog.findViewById(R.id.coupen_title);
                coupenExpiryDate = checkCoupenpriceDailog.findViewById(R.id.coupen_validity);
                coupenBody = checkCoupenpriceDailog.findViewById(R.id.coupen_body);
                footerText = checkCoupenpriceDailog.findViewById(R.id.footer_text);
                applyORremoveBtnContainer = checkCoupenpriceDailog.findViewById(R.id.apply_or_remove_btns_layout);
                removeCoupenBtn = checkCoupenpriceDailog.findViewById(R.id.remove_btn);
                applyCoupenBtn = checkCoupenpriceDailog.findViewById(R.id.apply_btn);

                footerText.setVisibility(View.GONE);
                applyORremoveBtnContainer.setVisibility(View.VISIBLE);
                originalPrice = checkCoupenpriceDailog.findViewById(R.id.original_price);
                discountedPrice = checkCoupenpriceDailog.findViewById(R.id.discounted_price);

                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                coupensRecyclerView.setLayoutManager(layoutManager);

                //for coupon dialog
                originalPrice.setText(productPrice.getText());
                productOriginalPrice = productPriceText;
                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(position,DBqueries.rewardModelList
                        , true
                        ,coupensRecyclerView, selectedCoupen, productOriginalPrice
                        ,coupenTitle, coupenExpiryDate, coupenBody
                        ,discountedPrice,cartItemModelList);
                coupensRecyclerView.setAdapter(myRewardsAdapter);
                myRewardsAdapter.notifyDataSetChanged();

                applyCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSeletedCoupenId())){
                            for (RewardModel rewardModel:DBqueries.rewardModelList) {
                                if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSeletedCoupenId())){
                                    rewardModel.setAlreadyUsed(true);
                                    coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.rewardbackground_image));
                                    coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                                    redeemBtn.setText("Coupen");
                                }
                            }

                            checkCoupenpriceDailog.dismiss();
                        }

                    }
                });

                removeCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel:DBqueries.rewardModelList) {
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSeletedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        coupenTitle.setText("coupen");
                        coupenExpiryDate.setText("validity");
                        coupenBody.setText("Tap the icon on the top right to select your coupon.");
                        coupensApplied.setVisibility(View.INVISIBLE);
                        coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                        coupenRedemptionBody.setText("Apply your coupen here");
                        redeemBtn.setText("Redeem ");
                        productPrice.setText("$"+productPriceText);
                        cartItemModelList.get(position).setSeletedCoupenId(null);
                        notifyItemChanged(cartItemModelList.size()-1);
                        checkCoupenpriceDailog.dismiss();
                    }
                });


                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSeletedCoupenId())){
                    for (RewardModel rewardModel:DBqueries.rewardModelList) {
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSeletedCoupenId())){
                            coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.rewardbackground_image));
                            coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                            redeemBtn.setText("Coupen");

                            coupenBody.setText(rewardModel.getCoupenBody());
                            if (rewardModel.getType().equals("Discount")){
                                coupenTitle.setText(rewardModel.getType());
                            }else{
                                coupenTitle.setText("Flat CAD "+ rewardModel.getDiscORamt() + " OFF");
                            }
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
                            coupenExpiryDate.setText("till " + simpleDateFormat.format(rewardModel.getTimestamp()));


                        }
                    }
                    discountedPrice.setText("CAD."+cartItemModelList.get(position).getDiscountedPrice());
                    coupensApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("CAD."+cartItemModelList.get(position).getDiscountedPrice());
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText)-Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    coupensApplied.setText("coupen applied -CAD. "+offerDiscountedAmt);

                }else{
                    coupensApplied.setVisibility(View.INVISIBLE);
                    coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    coupenRedemptionBody.setText("Apply your coupen here");
                    redeemBtn.setText("Redeem ");

                }


                productQuantity.setText("Qty: " + quantity);
                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.white)));
                        //productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));

                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.white)));
                        //productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    }
                }

                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogRecyclerView();
                    }
                });

                /////coupen dialog

                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);

                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max "+String.valueOf(maxQuantity));

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!TextUtils.isEmpty(quantityNo.getText())){
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0 ) {
                                        if (itemView.getContext() instanceof MainActivity) {
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        } else {
                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty: " + quantityNo.getText());

                                        notifyItemChanged(cartItemModelList.size()-1);


                                        if (!showDeleteBtn){
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQty = Integer.parseInt(quantity);
                                            final int finalQty = Integer.parseInt(quantityNo.getText().toString());
                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if(finalQty > initialQty){
                                                for (int y = 0; y < finalQty - initialQty; y++){
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timestamp = new HashMap<>();
                                                    timestamp.put("time", FieldValue.serverTimestamp());
                                                    final int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                                            .collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIDs.add(quantityDocumentName);

                                                                    if(finalY + 1 == finalQty - initialQty){
                                                                        firebaseFirestore.collection("PRODUCTS").document(productID)
                                                                                .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                                                .limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            List<String> serverQuantity = new ArrayList<>();

                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }

                                                                                            long availableQty = 0;
                                                                                            boolean noLongerAvailable = true;
                                                                                            for (String qtyId : qtyIDs){
                                                                                                if (!serverQuantity.contains(qtyId)){
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "All products may not be avaiable in required quantity", Toast.LENGTH_SHORT).show();
                                                                                                }else{
                                                                                                    availableQty++;
                                                                                                }
                                                                                            }
                                                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                                        }else{
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }else if (initialQty > finalQty){
                                                for (int x = 0; x < initialQty - finalQty; x++) {
                                                    final String qtyID = qtyIDs.get(qtyIDs.size() - 1 - x);
                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                                            .collection("QUANTITY").document(qtyID)
                                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            qtyIDs.remove(qtyID);
                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                            if (finalX +1 == initialQty - finalQty){
                                                                DeliveryActivity.loadingDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }else{
                                        Toast.makeText(itemView.getContext(),"Max quantity : "+maxQuantity.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });
                if(offersAppliedNo > 0){
                    offerApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(cuttedPriceText)-Long.valueOf(productPriceText));
                    offerApplied.setText("offer applied -CAD"+offerDiscountedAmt);
                }else{
                    offerApplied.setVisibility(View.INVISIBLE);
                }

            }
            else{
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setText("");
                coupenRedemptionLayout.setVisibility(View.GONE);
                freeCoupens.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
                coupensApplied.setVisibility(View.GONE);
                offerApplied.setVisibility(View.GONE);
                freeCoupenIcon.setVisibility(View.INVISIBLE);

                // productQuantity.setText("Qty: " + 0 );
                // productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
                // productQuantity.setTextColor(Color.parseColor("#70000000"));
                //productQuantity.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));

            }


            if(showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else{
                deleteBtn.setVisibility(View.GONE);
            }




            ////coupen dialog
            redeemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (RewardModel rewardModel:DBqueries.rewardModelList) {
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSeletedCoupenId())){
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    checkCoupenpriceDailog.show();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSeletedCoupenId())){
                        for (RewardModel rewardModel:DBqueries.rewardModelList) {
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSeletedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }


                        if (!ProductDetailsActivity.running_cart_query){
                        ProductDetailsActivity.running_cart_query = true;

                        DBqueries.removeFromCart(position,itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }
        private void showDialogRecyclerView() {
            if (coupensRecyclerView.getVisibility() == View.GONE) {
                coupensRecyclerView.setVisibility(View.VISIBLE);
                selectedCoupen.setVisibility(View.GONE);
            } else {
                coupensRecyclerView.setVisibility(View.GONE);
                selectedCoupen.setVisibility(View.VISIBLE);
            }
        }
    }

    class CartTotalAmountViewholder extends RecyclerView.ViewHolder{
        private TextView totalItems;
        private TextView totalItemPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;
        private TextView savedAmount;

        public CartTotalAmountViewholder(@NonNull View itemView) {
            super(itemView);

            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }
        private void setTotalAmount(int totalItemText,int totalItemPricetext,String deliveryPreicetext,int totalAmountText, int savedAmountText){
            totalItems.setText("Price ("+totalItemText+" items)");
            totalItemPrice.setText("CAD "+totalItemPricetext);
            if (deliveryPreicetext.equals("FREE")){
                deliveryPrice.setText(deliveryPreicetext);
            }else {
                deliveryPrice.setText("CAD "+deliveryPreicetext);
            }
            totalAmount.setText("CAD "+totalAmountText);
            cartTotalAmount.setText("CAD "+totalAmountText);
            savedAmount.setText("You saved CAD "+savedAmountText+" on this order.");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent();
            if(totalItemPricetext == 0){
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                if (showDeleteBtn){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            }else{
                parent.setVisibility(View.VISIBLE);
            }

        }
    }
}

