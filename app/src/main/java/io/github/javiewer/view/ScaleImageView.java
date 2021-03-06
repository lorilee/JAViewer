package io.github.javiewer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Project: JAViewer
 */
public class ScaleImageView extends ImageView {

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {

            /*if (getDrawable() == null) {
                setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_menu_actresses, null));
            }*/
            int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

            setMeasuredDimension(measuredWidth, measuredWidth);

        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}