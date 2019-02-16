package com.pitmasteriq.qsmart.monitor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pitmasteriq.qsmart.R;

public class IntegerParamEditor extends DialogFragment
{
    private static final String PARAM_SELECTOR = "param_selector";
    private static final String PARAM_CURRENT = "param_current";
    private static final String PARAM_MIN = "param_min";
    private static final String PARAM_MAX = "param_max";

    private TextView txtTitle, txtMin, txtMax;
    private int selector, current, min, max;
    private EditText edtValue;
    private Button btnCancel, btnOk;
    private OnParamEdited listener;

    public IntegerParamEditor(){}

    public static IntegerParamEditor newInstance(int selector, int current, int min, int max)
    {
        IntegerParamEditor frag = new IntegerParamEditor();
        Bundle args = new Bundle();
        args.putInt(PARAM_SELECTOR, selector);
        args.putInt(PARAM_CURRENT, current);
        args.putInt(PARAM_MIN, min);
        args.putInt(PARAM_MAX, max);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        selector = getArguments().getInt(PARAM_SELECTOR);
        current = getArguments().getInt(PARAM_CURRENT);
        min = getArguments().getInt(PARAM_MIN);
        max = getArguments().getInt(PARAM_MAX);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_integer_param_editor, container);
        getDialog().setTitle("Config Editor");

        txtTitle = v.findViewById(R.id.dia_int_title);
        txtMin = v.findViewById(R.id.dia_min);
        txtMax = v.findViewById(R.id.dia_max);
        edtValue = v.findViewById(R.id.dia_int_value);
        btnCancel = v.findViewById(R.id.dia_int_cancel);
        btnOk = v.findViewById(R.id.dia_int_ok);


        txtTitle.setText("Please enter a value");
        txtMin.setText("Min: " + min);
        txtMax.setText("Max: " + max);
        edtValue.setHint("Current: " + current);


        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(edtValue.getText().toString().length() > 0)
                {
                    try
                    {
                        int value = Integer.valueOf(edtValue.getText().toString());

                        if (value >= min && value <= max || value == 0) {
                            listener.onEdited(selector, Integer.valueOf(edtValue.getText().toString()));
                            getDialog().dismiss();
                        } else {
                            edtValue.setError("Value is out of range.");
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        edtValue.setError("Value is out of range.");
                    }
                }
                else
                {
                    getDialog().dismiss();
                }
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


        return v;
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

