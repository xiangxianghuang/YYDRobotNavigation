package com.yongyida.robot.navigation.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;

/**
 * Create By HuangXiangXiang 2018/8/27
 */
public class MorePopupWindow extends PopupWindow implements View.OnClickListener {

    private Context context ;
    private PopupWindow popupWindow ;

    private TextView renameTvw ;
    private TextView deleteTvw ;

    public MorePopupWindow(Context context){

        this.context = context ;
        this.popupWindow = new PopupWindow(context) ;
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setFocusable(true);
        this.popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        View view = onCreteView() ;
        this.popupWindow.setContentView(view);
    }



    private View onCreteView(){

        LayoutInflater inflater = LayoutInflater.from(context) ;
        View view = inflater.inflate(R.layout.popup_more, null);

        renameTvw = view.findViewById(R.id.rename_tvw) ;
        deleteTvw = view.findViewById(R.id.delete_tvw) ;
        renameTvw.setOnClickListener(this);
        deleteTvw.setOnClickListener(this);

        return view ;
    }


    @Override
    public void onClick(View v) {

        if(v == renameTvw){
            // 重命名

            final EditText editText = new EditText(context);
            DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        String newName = editText.getText().toString();

                        if(onDataChangeListener != null){

                            onDataChangeListener.onRename(newName);
                        }

                    }
                }
            };

            // 保存
            new AlertDialog.Builder(context)
                    .setTitle("重命名")
                    .setTitle("请输入新的巡线任务名称")
                    .setView(editText)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", mOnClickListener)
                    .create()
                    .show();


        }else if(v == deleteTvw){
            // 删除
            DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == DialogInterface.BUTTON_POSITIVE) {

                        if(onDataChangeListener != null){

                            onDataChangeListener.onDelete();
                        }

                    }
                }
            };
            new AlertDialog.Builder(context)
                    .setTitle("删除")
                    .setMessage("是否删除此巡线任务?")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", mOnClickListener)
                    .create()
                    .show();
        }

        popupWindow.dismiss();
    }



    public void show(View anchor){

        popupWindow.showAsDropDown(anchor,-150,-50);

    }



    /**数据变化*/
    private OnDataChangeListener onDataChangeListener ;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }

    public interface OnDataChangeListener{

        /**重命名*/
        void onRename(String newName) ;

        /**删除*/
        void onDelete() ;
    }




}
