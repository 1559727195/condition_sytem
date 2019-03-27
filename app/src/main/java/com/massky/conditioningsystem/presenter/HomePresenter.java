/*
  Copyright 2017 Sun Jian
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.massky.conditioningsystem.presenter;

import com.massky.conditioningsystem.base.BasePresenter;
import com.massky.conditioningsystem.di.module.EntityModule;
import com.massky.conditioningsystem.di.scope.ActivityScope;
import com.massky.conditioningsystem.get.GetCommonCount;
import com.massky.conditioningsystem.get.GetDeviceList;
import com.massky.conditioningsystem.presenter.contract.HomeContract;
import com.massky.conditioningsystem.sql.CommonBean;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;


/**
 * @author: sunjian
 * created on: 2017/9/19 下午5:05
 * description: https://github.com/crazysunj/CrazyDaily
 */
@ActivityScope
public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {

    private  GetCommonCount getCommonCount;
    private  GetDeviceList getDeviceList;

    @Inject
    HomePresenter(GetCommonCount getCommonCount, GetDeviceList getDeviceList) {
        this.getCommonCount = getCommonCount;
        this.getDeviceList = getDeviceList;
    }


    @Override
    public void getSqlCounts() {
        getCommonCount.sqlCounts(new GetCommonCount.Onresponse() {
            @Override
            public void onresult(List<Map> list) {
                mView.showsqlCounts(list);
            }
        });
    }

    @Override
    public void show_deviceList() {
        getDeviceList.show_deviceList(new GetDeviceList.Onresponse() {
            @Override
            public void onresult(List<Map> controller_show_list, List<CommonBean.controller> controller_list) {
                mView.show_deviceList(controller_show_list,controller_list);
            }
        });
    }
}

