package com.umbrella.budgetapp.Customs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umbrella.budgetapp.R;

public class DetailedProgressbar extends RelativeLayout {

    private TextView titleText;
    private TextView valueText;
    private ProgressBar progressBar;

    public DetailedProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.custom_detailed_progressbar, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DetailedProgressbar);

        CharSequence title = typedArray.getText(R.styleable.DetailedProgressbar_title);
        CharSequence value = typedArray.getText(R.styleable.DetailedProgressbar_value);
        int progress = typedArray.getInteger(R.styleable.DetailedProgressbar_progress_value, 0);
        int colors = typedArray.getColor(R.styleable.DetailedProgressbar_progress_color, getResources().getColor(R.color.colorAccent, null));

        typedArray.recycle();

        titleText = findViewById(R.id.Progressbar_Name);
        valueText = findViewById(R.id.Progressbar_Amount);
        progressBar = findViewById(R.id.Progressbar_Attr);

        setTitleText(title);
        setValueText(value);
        setProgressBar(progress, colors);
    }

    public CharSequence getTitleText() {
        return titleText.getText();
    }
    public CharSequence getValueText() {
        return valueText.getText();
    }
    public int getProgressBar() {
        return progressBar.getProgress();
    }
    public ColorStateList getProgressColor() {
        return  progressBar.getProgressTintList();
    }

    public void setTitleText(CharSequence value) {
        titleText.setText(value);
    }
    public void setValueText(CharSequence value) {
        valueText.setText(value);
    }
    public void setProgressBar(int value, int coloring) {
        progressBar.setProgress(value);
        progressBar.setProgressTintList(ColorStateList.valueOf(coloring));
    }

}