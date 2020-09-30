package com.umbrella.budgetapp.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.cache.Memory
import com.umbrella.budgetapp.extensions.currencyText
import java.math.BigDecimal

class DetailedProgressbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private var titleView: TextView
    private var valueView: TextView
    private var progressbar: ProgressBar

    var title: String? = ""
    set(value) {
        field = value
        titleView.text = value
    }

    var value : BigDecimal? = BigDecimal.ZERO
    set(value) {
        field = value
        valueView.currencyText(Memory.lastUsedCountry.symbol, value ?: BigDecimal.ZERO)
    }

    var progress : Int = 0
    set(value) {
        field = value
        progressbar.progress = value
    }

    var max : Int = 100
    set(value) {
        field = value
        progressbar.max = value
    }

    var progressBarColor : Int = resources.getColor(R.color.colorAccent, context.theme)
    set(value) {
        field = value
        progressbar.progressTintList = ColorStateList.valueOf(value)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_detailed_progressbar, this, true)

        titleView = findViewById(R.id.Progressbar_Name)
        valueView = findViewById(R.id.Progressbar_Amount)
        progressbar = findViewById(R.id.Progressbar_Attr)

        attrs?.let {
            val styledAttributes = context.obtainStyledAttributes(it, R.styleable.DetailedProgressbar, 0, 0)

            title = styledAttributes.getString(R.styleable.DetailedProgressbar_title)
            value = styledAttributes.getString(R.styleable.DetailedProgressbar_value)?.toBigDecimal()
            progress = styledAttributes.getInteger(R.styleable.DetailedProgressbar_progress_value, 0)
            progressBarColor = styledAttributes.getColor(R.styleable.DetailedProgressbar_progress_value, resources.getColor(R.color.colorAccent, context.theme))

            styledAttributes.recycle()
        }
    }
}