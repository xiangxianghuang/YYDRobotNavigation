package com.yongyida.robot.navigation.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yongyida.robot.util.LogHelper;

/**
 * Create By HuangXiangXiang 2018/8/27
 */
public class MapPathView extends View {

    private static final String TAG = MapPathView.class.getSimpleName() ;

    private Paint paint ;
    private Bitmap bgBitmap ;
    private Matrix matrix = new Matrix() ;

    public MapPathView(Context context) {

        super(context);
        init() ;
    }

    public MapPathView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        init() ;
    }


    private int viewWidth ;
    private int viewHeight ;

    private int bitmapWidth ;
    private int bitmapHeight ;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        boolean isChanged = false ;

        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();

        if(this.viewWidth != viewWidth){

            this.viewWidth = viewWidth ;
            isChanged = true ;
        }

        if(this.viewHeight != viewHeight){

            this.viewHeight = viewHeight ;
            isChanged = true ;
        }


        LogHelper.i(TAG, LogHelper.__TAG__() + ",viewWidth : " + viewWidth + ", viewHeight : " + viewHeight );
        if(bgBitmap != null){

            int bitmapWidth = bgBitmap.getWidth() ;
            int bitmapHeight = bgBitmap.getHeight() ;

            float sx = 1f * viewWidth / bitmapWidth ;
            float sy = 1f * viewHeight / bitmapHeight ;


            matrix.reset();
            if(sx < sy){    // 高度更小 上下居中

                float py = (viewHeight/2 - bitmapHeight*sx/2)/(1- sx);
                matrix.postScale(sx, sx, 0 , py) ;

            }else {// 宽度更小 左右居中

                float px = (viewWidth/2 - bitmapWidth*sy/2)/(1- sy);
                LogHelper.i(TAG, LogHelper.__TAG__() + ",px : " +px );

                matrix.postScale(sy, sy, px, 0) ;

            }

            LogHelper.i(TAG, LogHelper.__TAG__() + ",width : " + bgBitmap.getWidth() + ", height : " + bgBitmap.getHeight() );
        }

    }

    /***/
    private void init(){

        paint = new Paint(Paint.ANTI_ALIAS_FLAG) ;
        matrix = new Matrix() ;
    }

    public void setBgBitmap(Bitmap bgBitmap) {

        this.bgBitmap = bgBitmap;

        LogHelper.i(TAG, LogHelper.__TAG__() + ",viewWidth : " + viewWidth + ", viewHeight : " + viewHeight );
        if(bgBitmap != null){

            LogHelper.i(TAG, LogHelper.__TAG__() + ",width : " + bgBitmap.getWidth() + ", height : " + bgBitmap.getHeight() );
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(bgBitmap != null){

//            matrix.postScale(1f*bgBitmap.getWidth()/viewWidth, 1f*bgBitmap.getHeight()/viewHeight, 0, 0) ;

            canvas.drawBitmap(bgBitmap, matrix, paint);

        }





    }
}
