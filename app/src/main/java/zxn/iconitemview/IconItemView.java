package zxn.iconitemview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import zxn.gesturelock.app.R;

/**
 * 条目view.
 * Created by zxn on 2019/4/25.
 */
public class IconItemView extends RelativeLayout {
    protected TextView tvText;
    protected View vLine;
    protected TextView tvRightText;
    private Drawable mLeftIconDrawable;
    private Drawable mRightIconDrawable;
    private String mText;
    private int mLineColor;
    private int mTextColor;
    private int mLeftIconPadding;
    private int mRightIconPadding;
    private int mLeftMargin;
    private int mMinimumWidth;
    private String mRightText;
    private int mRightIconDrawableWidth;
    private int mRightTextPadding;
    private int mRightTextColor;
    private int mRightTextSize;
    private ImageView mIvRight;
    private boolean isRightTextSingleLine = true;
    private int mRightTextWidth;
    private boolean mShowLine;

    public IconItemView(Context context) {
        this(context, null);
    }

    public IconItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttributeSet(attrs);
        refreshView();
    }

    /**
     * 单位转换: sp  px
     *
     * @param context context
     * @param sp      sp
     * @return px
     */
    public static int sp2px(Context context, int sp) {
        return (int) (getFontDensity(context) * sp + 0.5);
    }

    public static float getFontDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    private void refreshView() {
        tvText.setCompoundDrawables(mLeftIconDrawable, null, mRightIconDrawable, null);
        if (!TextUtils.isEmpty(mText)) {
            tvText.setText(mText);
        }
        tvText.setTextColor(mTextColor);
        vLine.setBackgroundColor(mLineColor);
        vLine.setVisibility(mShowLine ? View.VISIBLE : INVISIBLE);
        tvText.setCompoundDrawablePadding(mLeftIconPadding);
        tvText.setPadding(0, 0, mRightIconPadding, 0);


        LayoutParams layoutParams = (LayoutParams) vLine.getLayoutParams();
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) params;
            mLeftMargin = marginLayoutParams.leftMargin;
        }

        int drawablePadding = tvText.getCompoundDrawablePadding();
        layoutParams.leftMargin = mMinimumWidth + drawablePadding + mLeftMargin;

        tvRightText.setTextColor(mTextColor);
        tvRightText.setText(mRightText);
        LayoutParams rightTextLayoutParams = (LayoutParams) tvRightText.getLayoutParams();
        rightTextLayoutParams.rightMargin = mRightIconPadding + mRightIconDrawableWidth + mRightTextPadding;
        if (mRightTextWidth != 0) {
            rightTextLayoutParams.width = mRightTextWidth;
        }
        tvRightText.setLayoutParams(rightTextLayoutParams);
        tvRightText.setTextColor(mRightTextColor);
        tvRightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);

        if (isRightTextSingleLine) {
            tvRightText.setMaxLines(1);
        } else {
            tvRightText.setMaxLines(Integer.MAX_VALUE);
        }

    }

    private void initAttributeSet(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconItemView);
        if (null != typedArray) {
            mLeftIconDrawable = typedArray.getDrawable(R.styleable.IconItemView_leftIcon);
            if (null != mLeftIconDrawable) {
                mMinimumWidth = mLeftIconDrawable.getMinimumWidth();
                mLeftIconDrawable.setBounds(0, 0, mLeftIconDrawable.getMinimumWidth(), mLeftIconDrawable.getMinimumHeight());
            }
            mRightIconDrawable = typedArray.getDrawable(R.styleable.IconItemView_rightIcon);
            if (null != mRightIconDrawable) {
                mRightIconDrawableWidth = mRightIconDrawable.getMinimumWidth();
                mRightIconDrawable.setBounds(0, 0, mRightIconDrawableWidth, mRightIconDrawable.getMinimumHeight());
            }
            mText = typedArray.getString(R.styleable.IconItemView_text);
            mLineColor = typedArray.getColor(R.styleable.IconItemView_lineColor, Color.WHITE);
            mTextColor = typedArray.getColor(R.styleable.IconItemView_textColor, Color.BLACK);
            mLeftIconPadding = typedArray.getDimensionPixelSize(R.styleable.IconItemView_leftIconPadding, 0);
            mRightIconPadding = typedArray.getDimensionPixelSize(R.styleable.IconItemView_rightIconPadding, 0);

            mRightText = typedArray.getString(R.styleable.IconItemView_rightText);
            mRightTextPadding = typedArray.getDimensionPixelSize(R.styleable.IconItemView_rightTextPadding, 0);
            mRightTextColor = typedArray.getColor(R.styleable.IconItemView_rightTextColor, Color.BLACK);
            mRightTextSize = typedArray.getDimensionPixelSize(R.styleable.IconItemView_rightTextSize, sp2px(getContext(), 14));

            isRightTextSingleLine = typedArray.getBoolean(R.styleable.IconItemView_rightTextSingleLine, true);
            mRightTextWidth = typedArray.getDimensionPixelSize(R.styleable.IconItemView_rightTextWidth, 0);
            mShowLine = typedArray.getBoolean(R.styleable.IconItemView_showLine, true);

            typedArray.recycle();
        }

    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_icon_item_view, this);
        tvText = (TextView) findViewById(R.id.tv_text);
        vLine = (View) findViewById(R.id.v_line);
        tvRightText = (TextView) findViewById(R.id.tv_right_text);
        mIvRight = (ImageView) findViewById(R.id.iv_right);

        if (getBackground() == null) {
            setBackgroundResource(R.drawable.bg_d_sc_default);
        }

    }

    /**
     * 设置右侧文字
     */
    public String getRightText() {
        //this.mRightText = rightText;
        return tvRightText.getText().toString();
    }

    public void setLeftText(String text) {
        this.mText = text;
        tvText.setText(mText);
    }

    public void setLeftTextColor(@ColorInt int color) {
        mTextColor = color;
        tvText.setTextColor(color);
    }

    /**
     * 设置右侧文字
     *
     * @param rightText 文字
     */
    public void setRightText(String rightText) {
        this.mRightText = rightText;
        tvRightText.setText(mRightText);
    }

    public void setRightTextColor(@ColorInt int color) {
        mRightTextColor = color;
        tvRightText.setTextColor(color);
    }

    public ImageView getIvRight() {
        return mIvRight;
    }

    /**
     * 设置右侧问题的icon.
     */
    public void setRightIcon(@DrawableRes int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        mRightIconDrawable = drawable;
        if (null != mRightIconDrawable) {
            mRightIconDrawableWidth = mRightIconDrawable.getMinimumWidth();
            mRightIconDrawable.setBounds(0, 0, mRightIconDrawableWidth, mRightIconDrawable.getMinimumHeight());
        }
        tvText.setCompoundDrawables(mLeftIconDrawable, null, mRightIconDrawable, null);

    }

    public TextView getTitleView() {
        return tvText;
    }

    public View getLineView() {
        return vLine;
    }

    public TextView getRightTextView() {
        return tvRightText;
    }


}
