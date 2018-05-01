package util.view;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsSeekBar;
import android.widget.SeekBar;


public class SeekBarL extends AbsSeekBar {

    private int height = -1;
    private int width = -1;
    public interface OnSeekBarChangeListener
    {
        public void onProgressChanged(SeekBarL vBar, int progress,boolean fromUser);
        public void onStartTrackingTouch(SeekBarL vBar);
        public void onStopTrackingTouch(SeekBarL vBar);
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    public SeekBarL(Context context)
    {
        this(context, null);
    }

    public SeekBarL(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public SeekBarL(Context context, AttributeSet attrs, int defstyle)
    {
        super(context, attrs, defstyle);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l)
    {
        mOnSeekBarChangeListener = l;
    }

    void onStartTrackingTouch()
    {
        if (mOnSeekBarChangeListener != null)
        {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onStopTrackingTouch()
    {
        if (mOnSeekBarChangeListener != null)
        {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    void onProgressRefresh(float scale, boolean fromUser)
    {
        Drawable thumb = null;
        try
        {
            Field mThumb_f = this.getClass().getSuperclass().getDeclaredField("mThumb");
            mThumb_f.setAccessible(true);
            thumb = (Drawable)mThumb_f.get(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        setThumbPos(getWidth(), thumb, scale, Integer.MIN_VALUE);

        invalidate();

        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, getProgress(), fromUser);
        }
    }

    private void setThumbPos(int w, Drawable thumb, float scale, int gap)
    {
        int available = 0;
        try
        {

            int up = getPaddingTop();
            int bottom = getPaddingBottom();

            available = getHeight() - up - bottom;
            int thumbWidth = thumb.getIntrinsicWidth();
            int thumbHeight = thumb.getIntrinsicHeight();
            available -= thumbWidth;

            //The extra space for the thumb to move on the track
            available += getThumbOffset() * 2;

            int thumbPos = (int) (scale * available);

            int topBound, bottomBound;
            if (gap == Integer.MIN_VALUE) {
                Rect oldBounds = thumb.getBounds();
                topBound = oldBounds.top;
                bottomBound = oldBounds.bottom;
            } else {
                topBound = gap;
                bottomBound = gap + thumbHeight;
            }
            // Canvas will be translated, so 0,0 is where we start drawing
            thumb.setBounds(thumbPos, topBound, thumbPos + thumbWidth, bottomBound);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        width = 30;
        height = View.MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(width, height);
    }

    protected void onDraw(Canvas c)
    {
        c.rotate(-90);
        c.translate(-height,0);
        super.onDraw(c);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(h, w, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        boolean mIsUserSeekable=true;
        try
        {
            Field mIsUserSeekable_f = this.getClass().getSuperclass().getDeclaredField("mIsUserSeekable");
            mIsUserSeekable_f.setAccessible(true);

            mIsUserSeekable = mIsUserSeekable_f.getBoolean(this);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }

        if (!mIsUserSeekable || !isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                onStartTrackingTouch();
                trackTouchEvent(event);
                break;

            case MotionEvent.ACTION_MOVE:
                trackTouchEvent(event);
                Method attemptClaimDrag;
                try
                {
                    attemptClaimDrag = this.getClass().getSuperclass().getDeclaredMethod("attemptClaimDrag");
                    attemptClaimDrag.setAccessible(true);
                    attemptClaimDrag.invoke(this);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case MotionEvent.ACTION_UP:
                trackTouchEvent(event);
                onStopTrackingTouch();
                setPressed(false);
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                setPressed(false);
                invalidate(); // see above explanation
                break;
        }
        return true;
    }

    protected void trackTouchEvent(MotionEvent event)
    {

        final int height = getHeight();
        final int available = height - getPaddingLeft() - getPaddingRight();
        int y = (int)(height - event.getY());
        float scale;
        float progress = 0;
        if (y < getPaddingLeft()) {
            scale = 0.0f;
        } else if (y > height - getPaddingRight()) {
            scale = 1.0f;
        } else {
            scale = (float)(y - getPaddingLeft()) / (float)available;
            float mTouchProgressOffset = 0.0f;
            try
            {
                Field mTouchProgressOffset_f = this.getClass().getSuperclass().getDeclaredField("mTouchProgressOffset");
                mTouchProgressOffset_f.setAccessible(true);
                mTouchProgressOffset = mTouchProgressOffset_f.getFloat(this);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            progress = mTouchProgressOffset;
        }

        final int max = getMax();
        progress += scale * max;

        try
        {
            Method setProgress = this.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setProgress", int.class,boolean.class);
            setProgress.setAccessible(true);
            setProgress.invoke(this, (int)progress, true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}