package com.pdv.go4lunch.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @BindView(R.id.nbr_distance_tv)
    TextView mTextViewDistance;
    @BindView(R.id.switch_settings_btn)
    Switch mSwitch;
    @BindView(R.id.settings_save_btn)
    Button mButtonSave;
    @BindView(R.id.seekBar)
    SeekBar mSeekBar;

    public static String KEY_PREFERENCES_NOTIFICATION = "NOTIFICATIONS";
    public static String KEY_PREFERENCES_DISTANCE = "DISTANCE";

    private SharedPreferences mSharedPreferences;
    private PlacesViewModel mPlacesViewModel;

    public SettingsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this,view);
        mPlacesViewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        setUpSeekBar();
        setUpSwitchBtn();
        setUpSaveBtn();
        return view;
    }

    private void setUpSeekBar() {
        int progress = mSharedPreferences.getInt(KEY_PREFERENCES_DISTANCE,100);
        mSeekBar.setProgress(progress);
        mTextViewDistance.setText(progress +"m");
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewDistance.setText(progress + "m");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setUpSwitchBtn(){
        boolean checked = mSharedPreferences.getBoolean(KEY_PREFERENCES_NOTIFICATION,true);
        mSwitch.setChecked(checked);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mSharedPreferences.edit().putBoolean(KEY_PREFERENCES_NOTIFICATION, true).apply();
                    Toast.makeText(getContext(),getResources().getString(R.string.notifications_request_granted),Toast.LENGTH_SHORT).show();
                }else {
                    mSharedPreferences.edit().putBoolean(KEY_PREFERENCES_NOTIFICATION, false).apply();
                    Toast.makeText(getContext(),getResources().getString(R.string.notifications_disable),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpSaveBtn() {
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences.edit().putInt(KEY_PREFERENCES_DISTANCE, mSeekBar.getProgress()).apply();
                mPlacesViewModel.refreshListNearestRestaurants(((Go4LunchApplication) getActivity().getApplication()).getMyLocation());
                Toast.makeText(getContext(),getResources().getString(R.string.settings_updated),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
