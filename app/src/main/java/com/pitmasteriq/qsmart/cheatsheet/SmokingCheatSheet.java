package com.pitmasteriq.qsmart.cheatsheet;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pitmasteriq.qsmart.R;

public class SmokingCheatSheet extends AppCompatActivity
{

    private Group woodFuelGroup;
    private RadioGroup meatGroup, fuelGroup, chartButtonsGroup;
    private TextView meatDescription, meatPreparation, fuelDescription;
    private TableLayout infoChart;

    private int meatIndex = 0;
    private int chartIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoking_cheat_sheet);

        chartButtonsGroup = findViewById(R.id.cs_chart_buttons_group);
        woodFuelGroup = findViewById(R.id.cs_wood_fuel_grp);
        infoChart = findViewById(R.id.cs_chart);

        meatDescription = findViewById(R.id.cs_description);
        meatPreparation = findViewById(R.id.cs_preparation);
        fuelDescription = findViewById(R.id.cs_fuel_description);


        meatDescription.setText(getResources().getStringArray(R.array.cs_meat_descriptions)[0]);
        meatPreparation.setText(getResources().getStringArray(R.array.cs_meat_preparation)[0]);
        fuelDescription.setText(getResources().getStringArray(R.array.cs_fuel_descriptions)[0]);


        meatGroup = findViewById(R.id.cs_meat_group);
        meatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.cs_beef:
                        meatDescription.setText(getResources().getStringArray(R.array.cs_meat_descriptions)[0]);
                        meatPreparation.setText(getResources().getStringArray(R.array.cs_meat_preparation)[0]);
                        meatIndex=0;
                        break;
                    case R.id.cs_pork:
                        meatDescription.setText(getResources().getStringArray(R.array.cs_meat_descriptions)[1]);
                        meatPreparation.setText(getResources().getStringArray(R.array.cs_meat_preparation)[1]);
                        meatIndex=1;
                        break;
                    case R.id.cs_poultry:
                        meatDescription.setText(getResources().getStringArray(R.array.cs_meat_descriptions)[2]);
                        meatPreparation.setText(getResources().getStringArray(R.array.cs_meat_preparation)[2]);
                        meatIndex=2;
                        break;
                    case R.id.cs_lamb:
                        meatDescription.setText(getResources().getStringArray(R.array.cs_meat_descriptions)[3]);
                        meatPreparation.setText(getResources().getStringArray(R.array.cs_meat_preparation)[3]);
                        meatIndex=3;
                        break;
                    case R.id.cs_venison:
                        meatDescription.setText(getResources().getStringArray(R.array.cs_meat_descriptions)[4]);
                        meatPreparation.setText(getResources().getStringArray(R.array.cs_meat_preparation)[4]);
                        meatIndex=4;
                        break;
                    case R.id.cs_seafood:
                        meatDescription.setText(getResources().getStringArray(R.array.cs_meat_descriptions)[5]);
                        meatPreparation.setText(getResources().getStringArray(R.array.cs_meat_preparation)[5]);
                        meatIndex=5;
                        break;
                }

                if(infoChart.getVisibility() == View.VISIBLE)
                {
                    loadTable();
                }
            }
        });


        fuelGroup = findViewById(R.id.cs_fuel_group);
        fuelGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.cs_briquettes:
                        fuelDescription.setText(getResources().getStringArray(R.array.cs_fuel_descriptions)[0]);
                        woodFuelGroup.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.cs_charcoal:
                        fuelDescription.setText(getResources().getStringArray(R.array.cs_fuel_descriptions)[1]);
                        woodFuelGroup.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.cs_wood:
                        fuelDescription.setText("");
                        woodFuelGroup.setVisibility(View.VISIBLE);
                        loadTable();
                        infoChart.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });


        chartButtonsGroup.setOnCheckedChangeListener(onChartCheckedChangeListener);
    }


    private RadioGroup.OnCheckedChangeListener onChartCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId)
        {
            if(R.id.cs_expand_wood_chart == checkedId)
            {
                chartIndex = 0;
            }

            else if(R.id.cs_expand_smoking_chart == checkedId)
            {
                chartIndex = 1;
            }

            loadTable();
            infoChart.setVisibility(View.VISIBLE);
        }
    };




    private void refreshMeatRankings()
    {
        int headerRows = 2;

        for(int i = headerRows; i<infoChart.getChildCount(); i++)
        {
            String[] elements = getResources().getStringArray(R.array.cs_wood_chart_meats)[i-headerRows].split(":");

            TableRow row = (TableRow) infoChart.getChildAt(i);
            ((TextView)row.getChildAt(3)).setText(elements[meatIndex]);
        }
    }



    private TableRow createHeaderRow(String h1, String h2, String h3, String h4, float w1, float w2, float w3, float w4, int size)
    {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv1 = new TextView(this);
        TextView tv2 = new TextView(this);
        TextView tv3 = new TextView(this);
        TextView tv4 = new TextView(this);

        tv1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w1));
        tv2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w2));
        tv3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w3));
        tv4.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w4));

        tv1.setText(h1);
        tv2.setText(h2);
        tv3.setText(h3);
        tv4.setText(h4);

        tv1.setTextSize(size);
        tv2.setTextSize(size);
        tv3.setTextSize(size);
        tv4.setTextSize(size);

        tv1.setTypeface(null, Typeface.BOLD);
        tv2.setTypeface(null, Typeface.BOLD);
        tv3.setTypeface(null, Typeface.BOLD);
        tv4.setTypeface(null, Typeface.BOLD);

        row.addView(tv1);
        row.addView(tv2);
        row.addView(tv3);
        row.addView(tv4);

        return row;
    }


    private void loadTable()
    {
        infoChart.removeAllViews();

        if(chartIndex == 0)
        {
            infoChart.addView(createHeaderRow("", "", "", "●● = Highly Recommended   ● = Recommended", .01f, .01f, .01f, .97f, 10));
            infoChart.addView(createHeaderRow("Type", "Strength", "Taste", "", .2f, .2f, .5f, .1f, 12));
            createRows(getResources().getStringArray(R.array.cs_wood_chart),
                    0.2f,
                    0.2f,
                    0.5f,
                    0.1f);

            refreshMeatRankings();
        }
        else if(chartIndex == 1)
        {
            infoChart.addView(createHeaderRow("Cut", "Time", "Smoker Temp", "Internal Temp", .25f, .25f ,.25f, .25f, 12));
            String[] array;
            switch(meatIndex)
            {
                case 0: array = getResources().getStringArray(R.array.cs_smoking_chart_beef);
                    break;
                case 1: array = getResources().getStringArray(R.array.cs_smoking_chart_pork);
                    break;
                case 2: array = getResources().getStringArray(R.array.cs_smoking_chart_poultry);
                    break;
                case 3: array = getResources().getStringArray(R.array.cs_smoking_chart_lamb);
                    break;
                case 4: array = getResources().getStringArray(R.array.cs_smoking_chart_venison);
                    break;
                case 5: array = getResources().getStringArray(R.array.cs_smoking_chart_seafood);
                    break;
                default: array = getResources().getStringArray(R.array.cs_smoking_chart_beef);
            }

            createRows(array,
                    0.25f,
                    0.25f,
                    0.25f,
                    0.25f);
        }
    }


    private void createRows(String[] array, float w1, float w2, float w3, float w4)
    {
        int count = 0;
        for(String s : array)
        {
            TextView tv1, tv2, tv3, tv4;

            String[] elements = s.split(":");

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            if (count % 2 == 0)
                row.setBackgroundColor(getColor(R.color.tableRowAccent));



            tv1 = new TextView(this);
            tv1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w1));
            if(elements.length >= 1)
                tv1.setText(elements[0]);
            row.addView(tv1);


            tv2 = new TextView(this);
            tv2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w2));
            if(elements.length >= 2)
                tv2.setText(elements[1]);
            row.addView(tv2);


            tv3 = new TextView(this);
            tv3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w3));
            if(elements.length >= 3)
                tv3.setText(elements[2]);
            row.addView(tv3);


            tv4 = new TextView(this);
            tv4.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, w4));
            if(elements.length >= 4)
                tv4.setText(elements[3]);
            row.addView(tv4);


            infoChart.addView(row);
            count++;
        }
    }
}
