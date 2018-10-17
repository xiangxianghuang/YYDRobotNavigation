package com.yongyida.robot.navigation.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.PointActionInfo;
import com.yongyida.robot.navigation.bean.path.ActionData;
import com.yongyida.robot.navigation.bean.path.BaseAction;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/29
 */
public class AddActionPopupWindow implements AdapterView.OnItemClickListener {

    private Context context;

    private PopupWindow popupWindow ;

    private ArrayAdapter mActionAdapter;
    private ListView mActionLvw;

    public AddActionPopupWindow(Context context) {

        this.context = context;

        this.popupWindow = new PopupWindow(context) ;
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setFocusable(true);
        this.popupWindow.setBackgroundDrawable(new ColorDrawable(0));

        View view = onCreteView() ;
        this.popupWindow.setContentView(view);
    }

    private View onCreteView() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_add_action, null);
        mActionLvw = view.findViewById(R.id.action_lvw);

        mActionAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, canAddTypeNames);
        mActionLvw.setAdapter(mActionAdapter);

        mActionLvw.setOnItemClickListener(this);

        return view;
    }

    private final BaseAction.Type[] TYPES = BaseAction.Type.values() ;

    private final ArrayList<BaseAction.Type> canAddTypes = new ArrayList<>();
    private final ArrayList<String> canAddTypeNames = new ArrayList<>();

    /**
     * 判断此条是否存在
     * */
    private boolean isContain(ArrayList<ActionData> actions, BaseAction.Type type){

        final int size = actions.size() ;
        for (int i = 0; i < size; i++) {

            ActionData action = actions.get(i) ;
            if(type == action.getAction().getType()){

                return true ;
            }
        }
        return false ;
    }


    private void findLeft(ArrayList<ActionData> actions){

        canAddTypes.clear();
        canAddTypeNames.clear();

        // 遍历全部，找出没有包括的类
        final int typeSize = TYPES.length ;
        for (int i = 0; i < typeSize ; i++) {

            BaseAction.Type type = TYPES[i] ;

            if(!isContain(actions, type)){

                canAddTypes.add(type);
                canAddTypeNames.add(type.name) ;
            }
        }

    }


    private int height = 92 ;
    public void show(View anchor, PointActionInfo point){

        findLeft(point.getActions()) ;

        if(canAddTypeNames.isEmpty()){

            Toast.makeText(context, "没有未添加的类型", Toast.LENGTH_SHORT).show();
        }else {

            mActionAdapter.notifyDataSetChanged();

            int width = anchor.getWidth() ;
            int height = anchor.getHeight() ;

            popupWindow.setWidth(width - 20);
            popupWindow.showAsDropDown(anchor, 10 , -(height + this.height * canAddTypeNames.size()));
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(onAddItemListener != null){

            onAddItemListener.onAddItem(canAddTypes.get(position));
        }

        popupWindow.dismiss();
    }

    private OnAddItemListener onAddItemListener ;
    public void setOnAddItemListener(OnAddItemListener onAddItemListener) {
        this.onAddItemListener = onAddItemListener;
    }

    public interface OnAddItemListener{

        void onAddItem(BaseAction.Type type);
    }




}
