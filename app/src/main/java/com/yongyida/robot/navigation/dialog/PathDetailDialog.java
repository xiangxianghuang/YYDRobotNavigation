package com.yongyida.robot.navigation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.PathInfo;
import com.yongyida.robot.navigation.view.MapPathView;
import com.yongyida.robot.util.Constant;

/**
 * Create By HuangXiangXiang 2018/8/27
 * 路径详细名称
 */
public class PathDetailDialog extends Dialog {

    private PathInfo pathInfo;
    private Bitmap bitmap;

    private MapPathView mMapPathMpv;

    public PathDetailDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_path_detail);

        this.mMapPathMpv = findViewById(R.id.map_path_mpv);
        if(bitmap != null){

            this.mMapPathMpv.setBgBitmap(bitmap);
        }

        Constant.initDialogSize(this);
    }


    public void setPathInfo(PathInfo pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if(mMapPathMpv != null){
            this.mMapPathMpv.setBgBitmap(bitmap);
        }
    }

}
