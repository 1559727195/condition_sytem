package com.massky.conditioningsystem.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import java.lang.reflect.Field;

public class MRadioButton extends FrameLayout implements Checkable {
    private boolean mChecked;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    public MRadioButton(Context context) {
        this(context, null);
    }

    public MRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        defStyleAttr = getInternalR("attr", "radioButtonStyle");
        final TypedArray a = context.obtainStyledAttributes(
                attrs, getInternalRS("styleable", "CompoundButton"), defStyleAttr, defStyleRes);
        if (a.hasValue(getInternalR("styleable", "CompoundButton_checked"))) {
            final boolean checked = a.getBoolean(getInternalR("styleable", "CompoundButton_checked"), false);
            setChecked(checked);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.w("MINE", "MotionEvent:" + event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes. This callback is used for internal purpose only.
     *
     * @param listener the callback to call on checked state change
     * @hide
     */
    void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

    @Override
    public void setChecked(boolean checked) {
        Log.e("MINE", "setChecked:" + checked);
        if (mChecked != checked) {
            mChecked = checked;
            setSelected(checked);
            refreshDrawableState();
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setSelected(mChecked);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.setSelected(selected);
        }
    }

    public int getInternalR(String v1, String v2) {
        int titleStyle = 0;
        try {
            Class clasz = Class.forName("com.android.internal.R$" + v1);//styleable
            Field field = clasz.getDeclaredField(v2);
            field.setAccessible(true);
            titleStyle = (Integer) field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Log.w("MINE", "getInternalR:" + titleStyle);
        return titleStyle;
    }

    public int[] getInternalRS(String v1, String v2) {
        int[] textAppearanceStyleArr = new int[0];
        try {
            Class clasz = Class.forName("com.android.internal.R$" + v1);//styleable
            Field field = clasz.getDeclaredField(v2);
            field.setAccessible(true);
            textAppearanceStyleArr = (int[]) field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Log.w("MINE", "getInternalRS:" + textAppearanceStyleArr);
        return textAppearanceStyleArr;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(View buttonView, boolean isChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    public void toggle() {
        if (!isChecked()) {
            setChecked(!mChecked);
        }
    }

}
