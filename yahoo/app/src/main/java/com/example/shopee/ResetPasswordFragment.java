package com.example.shopee;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordFragment extends Fragment {


    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private EditText registeredEmail;
    private Button resetPassword_Btn;
    private TextView  goBack;

    private FrameLayout parentFrameLayout;
    //private ViewGroup emailIconContainer;
    //private ImageView emailIconText;
   // private ProgressBar  progressBar;


    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password,container,false);

        registeredEmail = view.findViewById(R.id.forgot_password_registered_email);
        resetPassword_Btn = view.findViewById(R.id.reset_password_btn);
        goBack = view.findViewById(R.id.forgot_password_go_back);
        //emailIconContainer =  view.findViewById(R.id.forgot_password_email_icon_container);
        //emailIconText =  view.findViewById(R.id.forget_password_email_icon_txt);
        //progressBar =   view.findViewById(R.id.forget_password_progressbar);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        firebaseAuth = firebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registeredEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        resetPassword_Btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                //TransitionManager.beginDelayedTransition(emailIconContainer);
                //emailIconContainer.setVisibility(view.visible);
                //progressBar.set
                resetPassword_Btn.setEnabled(false);
                resetPassword_Btn.setTextColor(Color.argb(50,255,255,255));


                firebaseAuth.sendPasswordResetEmail(registeredEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task){
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(),"Email sent successfully!", Toast.LENGTH_LONG);
                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(),error, Toast.LENGTH_SHORT);
                                }

                                resetPassword_Btn.setEnabled(true);
                                resetPassword_Btn.setTextColor(Color.rgb(255,255,255));
                            }

                        });
            }


        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

    }
    private void checkInputs(){
        if(TextUtils.isEmpty(registeredEmail.getText())){
            resetPassword_Btn.setEnabled(false);
            resetPassword_Btn.setTextColor(Color.argb(50,255,255,255));
        }else{
            resetPassword_Btn.setEnabled(true);
            resetPassword_Btn.setTextColor(Color.rgb(255,255,255));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();

        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.signin_account,fragment).commit();
    }
}
