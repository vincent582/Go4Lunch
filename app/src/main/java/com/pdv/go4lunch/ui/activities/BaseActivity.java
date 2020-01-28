package com.pdv.go4lunch.ui.activities;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;

public class BaseActivity extends AppCompatActivity {

    /**
     * Error Handler
     * @return Toast with error occurred.
     */
    protected OnFailureListener mOnFailureListener(){
        return e -> Toast.makeText(getApplicationContext(),"Execption: "+e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
