package com.pitmasteriq.qsmart.monitor;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pitmasteriq.qsmart.R;

public class TimeParamEditor extends DialogFragment
{
    private static final String PARAM_SELECTOR = "param_selector";
    private static final String PARAM_CURRENT = "param_current";

    private int selector, current;
    private ImageButton btnAdd, btnSub;
    private TextView txtValue;
    private Button btnOk, btnCancel;
    private int newValue;
    private OnParamEdited listener;

    public TimeParamEditor(){}

    public static TimeParamEditor newInstance(int selector, int current)
    {
        TimeParamEditor frag = new TimeParamEditor();
        Bundle args = new Bundle();
        args.putInt(PARAM_SELECTOR, selector);
        args.putInt(PARAM_CURRENT, current);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        selector = getArguments().getInt(PARAM_SELECTOR);
        current = getArguments().getInt(PARAM_CURRENT);
        newValue = current;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_time_param_editor, container);

        txtValue = v.findViewById(R.id.time_edit_value);
        btnAdd = v.findViewById(R.id.time_edit_add);
        btnSub = v.findViewById(R.id.time_edit_subtract);
        btnOk = v.findViewById(R.id.time_edit_ok);
        btnCancel = v.findViewById(R.id.time_edit_cancel);

        updateValueString(newValue);

        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                newValue++;
                if(newValue > 96)
                    newValue = 0;

                updateValueString(newValue);
            }
        });

        btnSub.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                newValue--;
                if(newValue < 0)
                    newValue = 96;

                updateValueString(newValue);
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getDialog().dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onEdited(selector, newValue);
                getDialog().dismiss();
            }
        });

        return v;
    }



    private void updateValueString(int value)
    {
        int minutes = value * 15;
        int hours = (int) Math.floor(minutes / 60);
        minutes = minutes % 60;

        txtValue.setText(String.format("%02d:%02d", hours, minutes));
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnParamEdited)
        {
            listener = (OnParamEdited) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnParamEdited");
        }
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }
}
