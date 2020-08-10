package com.umbrella.budgetapp.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.umbrella.budgetapp.R

class DetailedProgressbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private var titleView: TextView
    private var valueView: TextView
    private var progressbar: ProgressBar

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_detailed_progressbar, this, true)

        titleView = findViewById(R.id.Progressbar_Name)
        valueView = findViewById(R.id.Progressbar_Amount)
        progressbar = findViewById(R.id.Progressbar_Attr)

        attrs?.let {
            val styledAttributes = context.obtainStyledAttributes(it, R.styleable.DetailedProgressbar, 0, 0)

            setTitle(title = styledAttributes.getString(R.styleable.DetailedProgressbar_title))
            setValue(value = styledAttributes.getString(R.styleable.DetailedProgressbar_value))
            setProgress(value = styledAttributes.getInteger(R.styleable.DetailedProgressbar_progress_value, 0))
            setProgressBarColor(color = styledAttributes.getColor(R.styleable.DetailedProgressbar_progress_value, resources.getColor(R.color.colorAccent, context.theme)))

            styledAttributes.recycle()
        }
    }

    private fun setTitle(title: String? = "") {
        titleView.text = title;
    }

    private fun setValue(value: String? = "") {
        valueView.text = value
    }

    private fun setProgress(value: Int = 0) {
        progressbar.progress = value
    }

    private fun setProgressBarColor(@ColorInt color: Int = resources.getColor(R.color.colorAccent, context.theme)) {
        progressbar.progressTintList = ColorStateList.valueOf(color)
    }
}