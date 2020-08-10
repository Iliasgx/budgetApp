package com.umbrella.budgetapp.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.umbrella.budgetapp.R

class ImageIcon @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private var img = 0
    private var color = 0
    private var background: View? = null
    private var image: ImageView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_image_icon, this, true)
    }

    fun setImg(image: Int) {
        img = image
        val arr = resources.obtainTypedArray(R.array.icons)
        this.image!!.setImageResource(arr.getResourceId(image, 0))
        arr.recycle()
    }

    fun setColor(color: Int) {
        this.color = color
        background!!.setBackgroundColor(color)
    }
}