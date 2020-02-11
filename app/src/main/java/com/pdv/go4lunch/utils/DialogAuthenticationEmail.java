package com.pdv.go4lunch.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pdv.go4lunch.R;

public class DialogAuthenticationEmail extends DialogFragment {

    public interface DialogAuthenticationListener{
        public void onDialogAuthenticationSignInClick(DialogAuthenticationEmail dialogAuthenticationEmail);
        public void onDialogAuthenticationRegisterClick(DialogAuthenticationEmail dialogAuthenticationEmail);
    }

    DialogAuthenticationListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.authentication_dialog,null))
                .setPositiveButton("SignIn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogAuthenticationSignInClick(DialogAuthenticationEmail.this);
                    }
                })
                .setNegativeButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogAuthenticationRegisterClick(DialogAuthenticationEmail.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (DialogAuthenticationListener) getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()+" must implement DialogAuthenticationListener");
        }
    }
}
