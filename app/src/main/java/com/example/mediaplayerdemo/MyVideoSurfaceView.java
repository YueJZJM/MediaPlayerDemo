package com.example.mediaplayerdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class MyVideoSurfaceView extends SurfaceView {

    // 视频宽度
    private int videoWidth;
    // 视频高度
    private int videoHeight;

    public MyVideoSurfaceView(Context context) {
        this(context, null);
    }

    public MyVideoSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        videoWidth = 0;
        videoHeight = 0;
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    /**
     * 根据视频的宽高设置SurfaceView的宽高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {
            // 获取测量模式和测量大小
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            // 分情况设置大小
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // layout_width = 确定值或match_parent
                // layout_height = 确定值或match_parent
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;
                // 做适配，不让视频拉伸，保持原来宽高的比例
                // for compatibility, we adjust size based on aspect ratio
                if ( videoWidth * height  < width * videoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * videoWidth / videoHeight;
                } else if ( videoWidth * height  > width * videoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * videoHeight / videoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // layout_width = 确定值或match_parent
                // layout_height = wrap_content
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                // 计算高多少，保持原来宽高的比例
                height = width * videoHeight / videoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // layout_width = wrap_content
                // layout_height = 确定值或match_parent
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                // 计算宽多少，保持原来宽高的比例
                width = height * videoWidth / videoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // layout_width = wrap_content
                // layout_height = wrap_content
                // neither the width nor the height are fixed, try to use actual video size
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        // 设置SurfaceView的宽高
        setMeasuredDimension(width, height);
    }

    /**
     * 调整大小
     * @param videoWidth
     * @param videoHeight
     */
    public void adjustSize(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) return;
        // 赋值自己的宽高
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        // 设置Holder固定的大小
        getHolder().setFixedSize(videoWidth, videoHeight);
        // 重新设置自己的大小
        requestLayout();
    }

}
