package com.example.shopee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_cart_query = false;
    public static boolean running_rating_query = false;
    public static Activity productDetailsActivity;

    private ViewPager productImagesViewPager;
    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView productPrice;
    private String productOriginalPrice;
    private TextView cuttedPrice;
    private ImageView codIdicator;
    private TextView averageRating;
    private TextView tvCodIndicator;
    private TextView totalRatingMiniView;
    private TabLayout viewpagerIndicator;

    private LinearLayout coupenRedemptionLayout;
    private Button coupenRedeemBtn;
    // missing code
    private TextView rewardTitle;
    private TextView rewardBody;

    /////Product description
    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private TextView productOnlyDescriptionBody;

    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private String productDescription;
    private String productOtherDatails;


    /////Product description

    // rating layout//
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingNumberContainer;
    private TextView totalRatingFigure;
    private LinearLayout ratingsProgressBarContainer;

    // rating layout //


    private Button buynowbtn;
    private LinearLayout addToCartBtn;
    public static MenuItem cartItem;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;

    public static FloatingActionButton AddToWishlistBtn;

    private FirebaseFirestore firebaseFirestore;

    ///coupen dialog
    private  TextView coupenTitle;
    private TextView coupenExpiryDate;
    private TextView coupenBody;
    private RecyclerView coupensRecyclerView;
    private LinearLayout selectedCoupen;
    private TextView discountedPrice;
    private TextView originalPrice;
    ////coupen dialog

    private Dialog signInDialog;
    private Dialog loadingDialog;
    private FirebaseUser currentUser;
    public static String productID;
    private TextView badgeCount;
    private boolean inStock = false;

    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewpagerIndicator = findViewById(R.id.viewpager_indicator);
        AddToWishlistBtn = findViewById(R.id.add_to_wishlist_btn);
        productDetailsViewPager = findViewById(R.id.product_details_view_pager);
        productDetailsTabLayout = findViewById(R.id.product_details_tab_layout);
        buynowbtn = findViewById(R.id.buy_now_btn);

        coupenRedeemBtn = findViewById(R.id.coupon_redemption_btn);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        codIdicator = findViewById(R.id.cod_indicator_imageview);
        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
        productDetailsTabsContainer = findViewById(R.id.product_details_tabs_container);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);
        totalRatings = findViewById(R.id.total_ratings);
        averageRating = findViewById(R.id.average_rating);
        ratingNumberContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progressbar_container);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        //code missing
        coupenRedemptionLayout = findViewById(R.id.coupon_redemption_layout);

        initialRating = -1;

        // loading dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////loading dialog

        /////coupen dialog
        final Dialog checkCoupenpriceDailog = new Dialog(ProductDetailsActivity.this);
        checkCoupenpriceDailog.setContentView(R.layout.coupen_redeem_dialog);
        checkCoupenpriceDailog.setCancelable(true);
        checkCoupenpriceDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        ImageView toggleRecyclerView = checkCoupenpriceDailog.findViewById(R.id.toggle_recyclerview);
        coupensRecyclerView = checkCoupenpriceDailog.findViewById(R.id.coupens_recyclerview);
        selectedCoupen = checkCoupenpriceDailog.findViewById(R.id.selected_coupen);
        coupenTitle = checkCoupenpriceDailog.findViewById(R.id.coupen_title);
        coupenExpiryDate = checkCoupenpriceDailog.findViewById(R.id.coupen_validity);
        coupenBody = checkCoupenpriceDailog.findViewById(R.id.coupen_body);

        originalPrice = checkCoupenpriceDailog.findViewById(R.id.original_price);
        discountedPrice = checkCoupenpriceDailog.findViewById(R.id.discounted_price);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        coupensRecyclerView.setLayoutManager(layoutManager);

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRecyclerView();
            }
        });

        ////coupen dialog


        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();
        productID = getIntent().getStringExtra("PRODUCT_ID");
        Log.i("ERRORProductID", productID + "AAAA");
        productID = productID.trim();
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    documentSnapshot = task.getResult();

                    firebaseFirestore.collection("PRODUCTS").document(productID)
                            .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                long productImageCount = Long.valueOf(documentSnapshot.get("no_of_product_images").toString());
                                for (long x = 1; x < productImageCount; x++) {
                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                }
                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                productImagesViewPager.setAdapter(productImagesAdapter);

                                productTitle.setText(documentSnapshot.get("product_title").toString());
                                averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                                totalRatingMiniView.setText("(" + (long) documentSnapshot.get("total_ratings") + ")ratings");

                                Log.i("ERROR", documentSnapshot.get("total_ratings").toString());
                                productPrice.setText("CAD " + documentSnapshot.get("product_price ").toString());

                                //for coupon dialog
                                originalPrice.setText(productPrice.getText());
                                productOriginalPrice = documentSnapshot.get("product_price ").toString();
                                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(DBqueries.rewardModelList
                                        , true
                                        ,coupensRecyclerView, selectedCoupen, productOriginalPrice
                                        ,coupenTitle, coupenExpiryDate, coupenBody
                                        ,discountedPrice);
                                coupensRecyclerView.setAdapter(myRewardsAdapter);
                                myRewardsAdapter.notifyDataSetChanged();
                                //for coupon dialog

                                cuttedPrice.setText("CAD " + documentSnapshot.get("cutted_price").toString());
                                if ((boolean) documentSnapshot.get("COD")) {
                                    codIdicator.setVisibility(View.VISIBLE);
                                    tvCodIndicator.setVisibility(View.VISIBLE);
                                } else {
                                    codIdicator.setVisibility(View.INVISIBLE);
                                    tvCodIndicator.setVisibility(View.INVISIBLE);
                                }
                                if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                    productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                    productDetailsOnlyContainer.setVisibility(View.GONE);
                                    productDescription = documentSnapshot.get("product_description").toString();

                                    productOtherDatails = documentSnapshot.get("product_other_details").toString();

                                    long totalSpecTitles = Long.valueOf(documentSnapshot.get("total_spec_titles").toString());
                                    for (long x = 1; x < totalSpecTitles; x++) {
                                        productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));

                                        long specTitleTotalFields = Long.valueOf(documentSnapshot.get("spec_title_" + x + "_total_fields").toString());
                                        for (long y = 1; y < specTitleTotalFields; y++) {
                                            //productSpecificationModelList.add(new ProductSpecificationModel(1,documentSnapshot.get("spec_title_"+x+"_field_"+y+"_name").toString(),documentSnapshot.get("spec_title_"+x+"_field_"+y+"value").toString()));
                                        }
                                    }
                                } else {
                                    productDetailsTabsContainer.setVisibility(View.GONE);
                                    productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                    productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                }

                                totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");


                                for (int x = 0; x < 5; x++) {
                                    TextView rating = (TextView) ratingNumberContainer.getChildAt(x);
                                    rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                                    ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                    int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                    progressBar.setMax(maxProgress);
                                    progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                                }

                                totalRatingFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                averageRating.setText(documentSnapshot.get("average_rating").toString());
                                productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDatails, productSpecificationModelList));
                                if (currentUser != null) {
                                    if (DBqueries.myRating.size() == 0) {
                                        DBqueries.loadRatingList(ProductDetailsActivity.this);
                                    }
                                    if (DBqueries.cartList.size() == 0) {
                                        DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false,badgeCount, new TextView(ProductDetailsActivity.this));
                                        //DBqueries.loadRatingList(ProductDetailsActivity.this);
                                    }
                                    if (DBqueries.wishList.size() == 0) {
                                        DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                                        DBqueries.loadRatingList(ProductDetailsActivity.this);
                                    }
                                    if (DBqueries.rewardModelList.size() == 0){
                                        DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog, false);
                                    }
                                    if (DBqueries.cartList.size() != 0
                                            && DBqueries.rewardModelList.size() != 0
                                            && DBqueries.wishList.size() != 0){
                                        loadingDialog.dismiss();
                                    }

                                } else {
                                    loadingDialog.dismiss();
                                }

                                if (DBqueries.myRatedIds.contains(productID)) {
                                    int index = DBqueries.myRatedIds.indexOf(productID);
                                    initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
                                    setRating(initialRating);
                                }
                                if (DBqueries.cartList.contains(productID)) {
                                    ALREADY_ADDED_TO_CART = true;
                                } else {
                                    ALREADY_ADDED_TO_CART = false;
                                }

                                if (DBqueries.wishList.contains(productID)) {
                                    ALREADY_ADDED_TO_WISHLIST = true;
                                    AddToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));

                                } else {
                                    // code missing
                                    ALREADY_ADDED_TO_WISHLIST = false;
                                }

                                if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){
                                    inStock = true;
                                    buynowbtn.setVisibility(View.VISIBLE);
                                    addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (currentUser == null) {
                                                signInDialog.show();
                                            } else {
                                                if (!running_cart_query) {
                                                    running_cart_query = true;
                                                    if (ALREADY_ADDED_TO_CART) {
                                                        running_cart_query = false;
                                                        Toast.makeText(ProductDetailsActivity.this,"Already added to cart!",Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        Map<String, Object> addproduct = new HashMap<>();
                                                        addproduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                                                        addproduct.put("list_size", (long) (DBqueries.cartList.size() + 1));

                                                        firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                                                .update(addproduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    if (DBqueries.cartItemModelList.size() != 0) {
                                                                        DBqueries.cartItemModelList.add( 0,new CartItemModel(CartItemModel.CART_ITEM, productID,documentSnapshot.get("product_image_1").toString()
                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , (long) documentSnapshot.get("free_coupens")
                                                                                , documentSnapshot.get("product_price ").toString()
                                                                                , documentSnapshot.get("cutted_price").toString()
                                                                                , (long)1
                                                                                , (long) documentSnapshot.get("offers_applied")
                                                                                , (long)0
                                                                                , inStock
                                                                                ,(long)documentSnapshot.get("max-quantity")
                                                                                ,(long)documentSnapshot.get("stock_quantity")));
                                                                    }

                                                                    ALREADY_ADDED_TO_CART = true;
                                                                    //AddToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                                                                    DBqueries.cartList.add(productID);
                                                                    Toast.makeText(ProductDetailsActivity.this, "Added to cart successfully!", Toast.LENGTH_SHORT).show();
                                                                    invalidateOptionsMenu();
                                                                    //AddToWishlistBtn.setEnabled(true);
                                                                    running_cart_query = false;
                                                                } else {
                                                                    //AddToWishlistBtn.setEnabled(true);
                                                                    running_cart_query = false;
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(ProductDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    inStock = false;
                                    buynowbtn.setVisibility(View.GONE);
                                    TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                                    outOfStock.setText("Out of Stock");
                                    outOfStock.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    outOfStock.setCompoundDrawables(null,null,null,null);
                                }
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewpagerIndicator.setupWithViewPager(productImagesViewPager, true);

        AddToWishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {


                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = DBqueries.wishList.indexOf(productID);
                            DBqueries.removeFromWishlist(index, ProductDetailsActivity.this);
                            AddToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        } else {
                            AddToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                            Map<String, Object> addproduct = new HashMap<>();
                            addproduct.put("product_ID_" + String.valueOf(DBqueries.wishList.size()), productID);
                            addproduct.put("list_size", (long) (DBqueries.wishList.size() + 1));
                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addproduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                                    if (DBqueries.wishlistModelList.size() != 0) {
                                                        DBqueries.wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_1").toString()
                                                                , documentSnapshot.get("product_full_title").toString()
                                                                , (long) documentSnapshot.get("free_coupens")
                                                                , documentSnapshot.get("average_rating_").toString()
                                                                , (long) documentSnapshot.get("total_ratings")
                                                                , documentSnapshot.get("product_price ").toString()
                                                                , documentSnapshot.get("cutted_price").toString()
                                                                , (boolean) documentSnapshot.get("COD")
                                                                , inStock));
                                                    }

                                                    ALREADY_ADDED_TO_WISHLIST = true;
                                                    AddToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                                                    DBqueries.wishList.add(productID);
                                                    Toast.makeText(ProductDetailsActivity.this, "Added to wishlist successfully!", Toast.LENGTH_SHORT).show();


                                    } else {
                            AddToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));

                           // AddToWishlistBtn.setEnabled(true);

                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                    running_wishlist_query = false;
                                }
                            });

                        }
                    }
                }
            }
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // rating layout

        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;

                                setRating(starPosition);
                                Map<String, Object> updateRating = new HashMap<>();
                                if (DBqueries.myRatedIds.contains(productID)) {
                                    TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);

                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starPosition - initialRating, true));
                                } else {
                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", calculateAverageRating(starPosition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }
                                firebaseFirestore.collection("PRODUCTS").document(productID)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> myRating = new HashMap<>();
                                            if (DBqueries.myRatedIds.contains(productID)) {
                                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                            } else {

                                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                                myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starPosition + 1);
                                            }


                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myRatedIds.contains(productID)) {
                                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productID), (long) (starPosition + 1));

                                                            TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);
                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));
                                                        } else {
                                                            DBqueries.myRatedIds.add(productID);
                                                            DBqueries.myRating.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingNumberContainer.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")ratings");
                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                            totalRatingFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));
                                                            Toast.makeText(ProductDetailsActivity.this, "Thank you for rating", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingfigures = (TextView) ratingNumberContainer.getChildAt(x);

                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                            int maxProgress = Integer.parseInt(totalRatingFigure.getText().toString());
                                                            progressBar.setMax(maxProgress);
                                                            progressBar.setProgress(Integer.parseInt(ratingfigures.getText().toString()));
                                                        }
                                                        initialRating = starPosition;
                                                        averageRating.setText(calculateAverageRating(0, true));
                                                        averageRatingMiniView.setText(calculateAverageRating(0, true));

                                                        if (DBqueries.wishList.contains(productID) && DBqueries.wishlistModelList.size() != 0) {
                                                            int index = DBqueries.wishList.indexOf(productID);
                                                            DBqueries.wishlistModelList.get(index).setRating(averageRating.getText().toString());
                                                            DBqueries.wishlistModelList.get(index).setTotalRatings(Long.parseLong(totalRatingFigure.getText().toString()));

                                                        }

                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }

                }


            });
        }
        // rating layout

        buynowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM, productID
                            ,documentSnapshot.get("product_image_1").toString()
                            , documentSnapshot.get("product_title").toString()
                            , (long) documentSnapshot.get("free_coupens")
                            , documentSnapshot.get("product_price ").toString()
                            , documentSnapshot.get("cutted_price").toString()
                            , (long)1
                            , (long) documentSnapshot.get("offers_applied")
                            , (long)0
                            , inStock
                            ,(long)documentSnapshot.get("max-quantity")
                            ,(long)documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                    if (DBqueries.addressesModelList.size() == 0) {
                        DBqueries.loadAddresses(ProductDetailsActivity.this, loadingDialog);
                    } else {
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });



        coupenRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCoupenpriceDailog.show();
            }
        });

        //////// signin dialog

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment.disableCloseBtn = true;
                Sign_upFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment.disableCloseBtn = true;
                Sign_upFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });

        /////// signin dialog


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            coupenRedemptionLayout.setVisibility(View.GONE);
        } else {
            coupenRedemptionLayout.setVisibility(View.VISIBLE);
        }
        if (currentUser != null) {
            if (DBqueries.myRating.size() == 0) {
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }

            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (DBqueries.rewardModelList.size() == 0){
                DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (DBqueries.cartList.size() != 0
                    && DBqueries.rewardModelList.size() != 0
                    && DBqueries.wishList.size() != 0){
                loadingDialog.dismiss();
            }

        } else {
            loadingDialog.dismiss();
        }

        if (DBqueries.myRatedIds.contains(productID)) {
            int index = DBqueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }
        if (DBqueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        if (DBqueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            AddToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));

        } else {
            AddToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
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

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));

            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));

            }
        }
    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingNumberContainer.getChildAt(5 - x);
            //Log.i("ERROR Rating", ratingNo.toString());
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingFigure.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingFigure.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.main_cart_icon);


            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.cart_black);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

       if (currentUser != null){
            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));

            }
           else{
                badgeCount.setVisibility(View.VISIBLE);
                if(DBqueries.cartList.size()< 99) {
                    badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                }else{
                    badgeCount.setText("99");
                }
            }
        }
            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                        MainActivity.showCart = true;
                        startActivity(cartIntent);

                    }
                }
            });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            //todo: Search icon code
            productDetailsActivity  = null;
            finish();
            return true;
        } else if (id == R.id.search_icon) {
            return true;
            //todo: notification icon code
        } else if (id == R.id.main_cart_icon) {
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                MainActivity.showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }
}
