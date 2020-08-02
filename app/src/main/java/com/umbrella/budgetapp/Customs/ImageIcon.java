package com.umbrella.budgetapp.Customs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;

import com.umbrella.budgetapp.R;

public class ImageIcon extends RelativeLayout {
    @StyleableRes
    private int img;
    @StyleableRes
    private int color;

    private View background;
    private ImageView image;

    public ImageIcon(Context context) {
        super(context);
    }

    public ImageIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageIcon, 0,0);
        img = typedArray.getInt(R.styleable.ImageIcon_img, 0);
        color = typedArray.getColor(R.styleable.ImageIcon_color, 0);
        typedArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_image_icon, this);
    }

    public int getImg() {
        return img;
    }

    public int getColor() {
        return color;
    }

    public void setImg(int image) {
        this.img = image;
        final TypedArray arr = getResources().obtainTypedArray(R.array.icons);
        this.image.setImageResource(arr.getResourceId(image, 0));
        arr.recycle();
    }

    public void setColor(int color) {
        this.color = color;
        this.background.setBackgroundColor(color);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        background = this.findViewById(R.id.custom_imageIcon_Background);
        image = this.findViewById(R.id.custom_imageIcon_Src);
        setImg(getImg());
        setColor(getColor());
    }
}

/*
public class ValueView extends RelativeLayout {
    private void initializeViews(@NotNull Context context, AttributeSet attrs){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ValueView, 0, 0);
        mValue = typedArray.getString(R.styleable.ValueView_value);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.ValueView_valueSize, findSrc(R.dimen.defaultCustomValueTextSize));
        mCostType = typedArray.getInt(R.styleable.ValueView_costType, 3);
        mImageSize = typedArray.getDimensionPixelSize(R.styleable.ValueView_imageSize, findSrc(R.dimen.defaultCustomValueImageSize));
        typedArray.recycle();
    }

    public void setValue(String mValue) {
        view.setText(!Objects.equals(mValue, null) ? mValue : "0");
    }

    public void setTextSize(@StyleableRes int mTextSize) {
        if (mTextSize != findSrc(R.dimen.defaultCustomValueTextSize)){
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        } else {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        }
    }

    public void setType(int mCostType) {
        view.setTextColor(mCostType != 3 ? getResources().getIntArray(R.array.costTypeColor_array)[mCostType] : getResources().getColor(R.color.gold, null));
        final TypedArray arr = getResources().obtainTypedArray(R.array.costType_array);
        imgView.setImageResource(mCostType != 3 ? arr.getResourceId(mCostType, 3) : 0);
        imgView.setVisibility(mCostType != 3 ? VISIBLE : GONE);
        arr.recycle();
    }

    public void setImageSize(@StyleableRes int mImageSize) {
        int px;
        if (mImageSize != findSrc(R.dimen.defaultCustomValueImageSize)){
            px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mImageSize, getResources().getDisplayMetrics()));
        } else {
            px = Math.round(findSrc(R.dimen.defaultCustomValueImageSize) * getResources().getDisplayMetrics().density);
        }
        imgView.getLayoutParams().height = px;
        imgView.getLayoutParams().width = px;
    }


    private int findSrc(@DimenRes int dim){
        return (int) (getResources().getDimensionPixelSize(dim) / getResources().getDisplayMetrics().density);
    }
}
 */