package com.example.ft_hangouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.ft_hangouts.databinding.FragmentSettingBinding;

public class settingFragment extends Fragment
{
    private FragmentSettingBinding binding;

    public settingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_setting, container, false);

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        int theme = MainActivity.getCurrentTheme();

        switch(theme)
        {
            case R.style.Theme_Alliance:
                binding.radio.check(R.id.radioButton_Alliance);
                break;
            case R.style.Theme_Federation:
                binding.radio.check(R.id.radioButton_Federation);
                break;
            case R.style.Theme_Order:
                binding.radio.check(R.id.radioButton_Order);
                break;
            case R.style.Theme_42:
                binding.radio.check(R.id.radioButton_42);
                break;
            default:
                binding.radioButtonDefault.setChecked(true);
                break;
        }

        binding.radio.setOnCheckedChangeListener((radioGroup, checkId) ->
        {
            switch (radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()).getId())
            {
                case (R.id.radioButton_Alliance):
                    MainActivity.changeTheme(R.style.Theme_Alliance);
                    break;
                case (R.id.radioButton_Federation):
                    MainActivity.changeTheme(R.style.Theme_Federation);
                    break;
                case (R.id.radioButton_Order):
                    MainActivity.changeTheme(R.style.Theme_Order);
                    break;
                case (R.id.radioButton_42):
                    MainActivity.changeTheme(R.style.Theme_42);
                    break;
                default:
                    MainActivity.changeTheme(R.style.Theme_Ft_hangouts);
                    break;
            }
            FragmentActivity activity = getActivity();
            if(activity != null)
            {
                activity.finish();
                activity.startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return (binding.getRoot());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}