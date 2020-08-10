package com.umbrella.budgetapp.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.umbrella.budgetapp.R

class EmptyRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {
    private val _bg : ConstraintLayout
    private val _inner : ConstraintLayout
    private val _icon : ImageView
    private val _title : TextView
    private val _description : TextView

    private val defaultView : View = inflate(context, R.layout.empty_recycler_view, this)

    private var emptyView: View = defaultView
    private var showEmptyView : Boolean = true

    init {
        emptyView = defaultView

        _bg = findViewById(R.id.emptyDefaultRecyclerView_background)
        _inner = findViewById(R.id.emptyDefaultRecyclerView_layout)
        _icon = findViewById(R.id.emptyDefaultRecyclerView_icon)
        _title = findViewById(R.id.emptyDefaultRecyclerView_title)
        _description = findViewById(R.id.emptyDefaultRecyclerView_description)

        attrs?.let {
            val styled = context.obtainStyledAttributes(it, R.styleable.EmptyRecyclerView, 0, 0)

            val bgColor = styled.getColor(R.styleable.EmptyRecyclerView_customBackgroundColor, context.getColor(R.color.emptyRecyclerView_defaultBackgroundColor))
            val bgDrw = styled.getDrawable(R.styleable.EmptyRecyclerView_customBackgroundDrawable)
            val iconDrw = styled.getDrawable(R.styleable.EmptyRecyclerView_customIcon)
            val iconSize = styled.getDimensionPixelSize(R.styleable.EmptyRecyclerView_customIconSize, context.resources.getDimensionPixelSize(R.dimen.emptyRecyclerView_defaultImageSize))
            val title = styled.getString(R.styleable.EmptyRecyclerView_customTitle)
            val titleCaps = styled.getBoolean(R.styleable.EmptyRecyclerView_customTitleAllCaps, false)
            val titleTxtSize = styled.getDimensionPixelSize(R.styleable.EmptyRecyclerView_customTitleTextSize, context.resources.getDimensionPixelSize(R.dimen.emptyRecyclerView_defaultTitleTextSize))
            val titleTxtColor = styled.getColor(R.styleable.EmptyRecyclerView_customTitleTextColor, context.getColor(R.color.emptyRecyclerView_defaultTitleTextColor))
            val description = styled.getString(R.styleable.EmptyRecyclerView_customDescription)
            val descriptionTxtSize = styled.getDimensionPixelSize(R.styleable.EmptyRecyclerView_customDescriptionTextSize, context.resources.getDimensionPixelSize(R.dimen.emptyRecyclerView_defaultDescriptionTextSize))
            val descriptionTxtColor = styled.getColor(R.styleable.EmptyRecyclerView_customDescriptionTextColor, context.getColor(R.color.emptyRecyclerView_defaultDescriptionTextColor))

            styled.recycle()

            if (bgDrw != null) setCustomBackgroundDrawable(bgDrw) else setCustomBackgroundColor(bgColor)
            setIconDrawable(iconDrw)
            setIconSize(iconSize)
            setTitle(title)
            setTitleAllCaps(titleCaps)
            setTitleTextSize(titleTxtSize)
            setTitleTextColor(titleTxtColor)
            setDescription(description)
            setDescriptionTextSize(descriptionTxtSize)
            setDescriptionTextColor(descriptionTxtColor)
        }
    }

    private val emptyObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            val adapter = adapter
            if (adapter != null) {
                if (adapter.itemCount == 0 && showEmptyView) {
                    emptyView.visibility = View.VISIBLE
                    this@EmptyRecyclerView.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    this@EmptyRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)
        emptyObserver.onChanged()
    }

    fun setEmptyView(emptyViewId: Int?) {
        if (emptyViewId != null) {
            this.emptyView = View.inflate(context, emptyViewId, this)
        } else {
            this.emptyView = defaultView
        }
    }

    fun disableEmptyView(disable : Boolean) {
        showEmptyView = !disable
    }

    private fun setCustomBackgroundColor(colorId: Int) {
        _bg.setBackgroundColor(colorId)
    }

    private fun setCustomBackgroundDrawable(drawable: Drawable) {
        _bg.background = drawable
    }

    private fun setIconDrawable(drawable: Drawable?) {
        _icon.setImageDrawable(drawable ?: context.getDrawable(R.drawable.empty_list))
    }

    private fun setIconSize(size: Int?) {
        val def = if (size != null) {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Integer.valueOf(size).toFloat(), resources.displayMetrics).toInt()
        } else {
            context.resources.getDimensionPixelSize(R.dimen.emptyRecyclerView_defaultImageSize)
        }
        _icon.layoutParams.width = def
        _icon.layoutParams.height = def
        requestLayout()
    }

    private fun setTitle(title: CharSequence?) {
        _title.text = title ?: context.getString(R.string.emptyRecyclerView_defaultTitle)
    }

    private fun setTitleAllCaps(allCaps: Boolean) {
        _title.isAllCaps = allCaps
    }

    private fun setTitleTextSize(size: Int) {
        _title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat())
    }

    private fun setTitleTextColor(colorId: Int) {
        _title.setTextColor(colorId)
    }

    private fun setDescription(description: CharSequence?) {
        _description.text = description ?: context.getString(R.string.emptyRecyclerView_defaultDescription)
    }

    private fun setDescriptionTextSize(size: Int) {
        _description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat())
    }

    private fun setDescriptionTextColor(colorId: Int) {
        _description.setTextColor(colorId)
    }
}