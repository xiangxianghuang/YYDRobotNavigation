package com.yongyida.robot.http;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author 作    者：Hu Tao
 * @filename 文件名：com.yongyida.robot.resourcemanager.http.ApiService
 * @date 时    间：2017/8/15 0015
 * @Copyright 版    权：勇艺达机器人公司源代码，版权归勇艺达机器人公司所有。。
 * @description 描    述：TODO
 */
public interface ApiService {

    String THIRD_INFO_URL = "/y50bpro/type_album/query";
    String FOURTH_INFO_URL = "/y50bpro/album/query";


//    @Headers("Contexttent-Type: application/x-www-form-urlencoded;charset=UTF-8")
//    @POST(THIRD_INFO_URL)
//    Observable<ThirdDownInfo> searchThird(@Body ThirdUpInfo upInfo);


}
