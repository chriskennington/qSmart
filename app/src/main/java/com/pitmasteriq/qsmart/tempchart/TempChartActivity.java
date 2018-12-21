package com.pitmasteriq.qsmart.tempchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pitmasteriq.qsmart.R;
import com.pitmasteriq.qsmart.monitor.LogTag;
import com.pitmasteriq.qsmart.monitor.Temperature;

import java.util.ArrayList;

public class TempChartActivity extends AppCompatActivity
{

    private RadioGroup meatGroup, doneGroup;
    private ProgressBar progress;
    private TextView meatTempF, meatTempC, tempDesc;

    private int[][] values = new int[][]
            {
                    {110, 120, 130, 135, 145, 155},
                    {0, 120, 130, 135, 145, 155},
                    {0, 0, 0, 0, 0, 165},
                    {110, 120, 130, 135, 145, 155},
                    {110, 120, 130, 135, 145, 155},
                    {0, 0, 0, 135, 0, 0}
            };

    private String[][] names = new String[][]
            {
                    {"Bleu", "Rare", "Med. Rare", "Medium", "Med. Well", "Well"},
                    {"", "Rare", "Med. Rare", "Medium", "Med. Well", "Well"},
                    {"", "", "", "", "", "Safe"},
                    {"Bleu", "Rare", "Med. Rare", "Medium", "Med. Well", "Well"},
                    {"Bleu", "Rare", "Med. Rare", "Medium", "Med. Well", "Well"},
                    {"", "", "", "Medium", "", ""},
            };

    private ArrayList<RadioButton> buttons = new ArrayList<>();


    int meatIndex = 0;
    int doneIndex = 2;
    int desired = values[meatIndex][doneIndex];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meat_temp_chart);

        meatGroup = findViewById(R.id.mt_meat_group);
        doneGroup = findViewById(R.id.mt_doneness_group);
        progress = findViewById(R.id.mt_progress);
        meatTempF = findViewById(R.id.mt_tempf);
        meatTempC = findViewById(R.id.mt_tempc);
        tempDesc = findViewById(R.id.mt_temp_description);

        buttons.add((RadioButton) findViewById(R.id.mt_blue));
        buttons.add((RadioButton) findViewById(R.id.mt_rare));
        buttons.add((RadioButton) findViewById(R.id.mt_med_rare));
        buttons.add((RadioButton) findViewById(R.id.mt_med));
        buttons.add((RadioButton) findViewById(R.id.mt_med_well));
        buttons.add((RadioButton) findViewById(R.id.mt_well));

        progress.setMax(165);
        progress.setProgress(values[meatIndex][doneIndex]);
        //updateTextView();

        tempDesc.setText(getResources().getStringArray(R.array.meat_temp_descriptions)[doneIndex]);


        meatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.mt_beef:
                        meatIndex = 0;
                        break;
                    case R.id.mt_pork:
                        meatIndex = 1;
                        break;
                    case R.id.mt_poultry:
                        meatIndex = 2;
                        break;
                    case R.id.mt_lamb:
                        meatIndex = 3;
                        break;
                    case R.id.mt_venison:
                        meatIndex = 4;
                        break;
                    case R.id.mt_seafood:
                        meatIndex = 5;
                        break;
                }
                updateTempButtons();
                desired = values[meatIndex][doneIndex];

                //ProgressBarAnimation anim = new ProgressBarAnimation(progress, progress.getProgress(), desired);
                //anim.setDuration(500);
                //progress.startAnimation(anim);

                progress.setProgress(desired);
                updateTextView();
            }
        });


        doneGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.mt_blue:
                        doneIndex = 0;
                        break;
                    case R.id.mt_rare:
                        doneIndex = 1;
                        break;
                    case R.id.mt_med_rare:
                        doneIndex = 2;
                        break;
                    case R.id.mt_med:
                        doneIndex = 3;
                        break;
                    case R.id.mt_med_well:
                        doneIndex = 4;
                        break;
                    case R.id.mt_well:
                        doneIndex = 5;
                        break;
                }

                tempDesc.setText(getResources().getStringArray(R.array.meat_temp_descriptions)[doneIndex]);
                desired = values[meatIndex][doneIndex];

                /*ProgressBarAnimation anim = new ProgressBarAnimation(progress, progress.getProgress(), desired);
                anim.setDuration(500);
                progress.startAnimation(anim);*/

                progress.setProgress(desired);
                updateTextView();
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        updateTextView();
    }


    private void updateTempButtons()
    {
        int index = 0;
        for(RadioButton b : buttons)
        {
            if(values[meatIndex][index] > 0)
            {
                b.setText(names[meatIndex][index]);
                b.setVisibility(View.VISIBLE);
            }
            else
            {
                b.setVisibility(View.GONE);
            }

            index++;
        }

        if(meatIndex == 2)
            doneIndex = 5;

        if(meatIndex == 5)
            doneIndex = 3;
    }



    private void updateTextView()
    {
        int pHeight = progress.getHeight();
        float pY = progress.getY();
        float pBottom = pY + pHeight;

        float percent = desired / 165f;
        float y = pBottom - (pHeight * percent);

        Log.e(LogTag.DEBUG, "------------------------------------");
        Log.e(LogTag.DEBUG, "pHeight = " + pHeight);
        Log.e(LogTag.DEBUG, "pY = " + pY);
        Log.e(LogTag.DEBUG, "pBottom = " + pBottom);
        Log.e(LogTag.DEBUG, "percent = " + percent);
        Log.e(LogTag.DEBUG, "y = " + y);


        meatTempF.setText(String.valueOf(desired));
        meatTempC.setText(String.valueOf(Temperature.f2c(desired)));

        meatTempF.setY(y);
        meatTempC.setY(y);
    }




    public class ProgressBarAnimation extends Animation
    {
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }
    }
}
