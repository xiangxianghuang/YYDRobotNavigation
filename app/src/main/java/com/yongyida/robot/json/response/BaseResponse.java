package com.yongyida.robot.json.response;

import com.yongyida.robot.json.Base;

/**
 * Create By HuangXiangXiang 2018/11/2
 */
public abstract class BaseResponse extends Base {

    /**
     * 是否初始化地图
     * */
    private boolean initMap ;

    public boolean isInitMap() {
        return initMap;
    }

    public void setInitMap(boolean initMap) {
        this.initMap = initMap;
    }
}
