package com.example.shopee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyordersFragment extends Fragment {


    public MyordersFragment() {
        // Required empty public constructor
    }
    private RecyclerView myordersRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myorders, container, false);

        myordersRecyclerView = view.findViewById(R.id.my_orders_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myordersRecyclerView.setLayoutManager(layoutManager);

        List<MyorderItemModel> myorderItemModelList = new ArrayList<>();
        myorderItemModelList.add(new MyorderItemModel(R.drawable.product_image,2,"pixel 2XL (BLACK)","DELIVERED ON MONDAY 15JAN 2019"));
        myorderItemModelList.add(new MyorderItemModel(R.drawable.product_image,3,"pixel 2XL (BLACK)","DELIVERED ON MONDAY 15JAN 2019"));
        myorderItemModelList.add(new MyorderItemModel(R.drawable.product_image,1,"pixel 2XL (BLACK)","Cancelled"));
        myorderItemModelList.add(new MyorderItemModel(R.drawable.product_image,4,"pixel 2XL (BLACK)","DELIVERED ON MONDAY 15JAN 2019"));

        MyorderAdapter myorderAdapter = new MyorderAdapter(myorderItemModelList);
        myordersRecyclerView.setAdapter(myorderAdapter);
        myorderAdapter.notifyDataSetChanged();

        return view;
    }

}