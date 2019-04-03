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
import com.massky.conditioningsystem.di.scope.ActivityScope;
import com.massky.conditioningsystem.get.GetCommonCount;
import com.massky.conditioningsystem.get.GetDeviceList;
import com.massky.conditioningsystem.get.GetGroupList;
import com.massky.conditioningsystem.get.GetOperateId;
import com.massky.conditioningsystem.get.GetSceneList;
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

    private GetCommonCount getCommonCount;
    private GetDeviceList getDeviceList;
    private GetSceneList getSceneList;
    private GetGroupList getGroupList;
    private GetOperateId getOperateId;

    @Inject
    HomePresenter(GetCommonCount getCommonCount, GetDeviceList getDeviceList, GetSceneList getSceneList
            , GetGroupList getGroupList, GetOperateId getOperateId) {
        this.getCommonCount = getCommonCount;
        this.getDeviceList = getDeviceList;
        this.getSceneList = getSceneList;
        this.getGroupList = getGroupList;
        this.getOperateId = getOperateId;
    }

    @Override
    public void getSqlCounts() {
        getCommonCount.sqlCounts(new GetCommonCount.Onresponse() {
            @Override
            public void onresult(List<Map> list) {
                if (mView != null)
                    mView.showsqlCounts(list);
            }
        });
    }

    @Override
    public void show_deviceList(String trim) {
        getDeviceList.show_deviceList(trim, new GetDeviceList.Onresponse() {
            @Override
            public void onresult(List<Map> controller_show_list, List<CommonBean.controller> controller_list) {
                if (mView != null)
                    mView.show_deviceList(controller_show_list, controller_list);
            }
        });
    }

    @Override
    public void show_sceneList(String trim) {
        getSceneList.show_sceneList(trim, new GetSceneList.Onresponse() {

            @Override
            public void onresult(List<Map> scene_show_list, List<CommonBean.scene> scene_list) {
                if (mView != null)
                    mView.show_sceneList(scene_show_list, scene_list);
            }
        });
    }

    @Override
    public void show_controlList(String trim) {
        getGroupList.show_groupList(trim, new GetGroupList.Onresponse() {
            @Override
            public void onresult(List<Map> group_show_list, List<CommonBean.group> group_list) {
                if (mView != null)
                    mView.show_groupList(group_show_list, group_list);
            }
        });
    }

    @Override
    public void show_control_device(String sql, CommonBean.operate operate, String selectMaxId) {
        getOperateId.show_operateId(new GetOperateId.Onresponse() {

            @Override
            public void onresult(long operate_max_id) {
                mView.show_operate_max_id(operate_max_id);
            }
        }, sql, operate, selectMaxId);
    }

    @Override
    public void show_operateStatus(long operate_max_id) {
        getOperateId.show_operateStatus(operate_max_id, new GetOperateId.Onresponse_1() {
            @Override
            public void onresult(List<CommonBean.operate> operate_list) {
                mView.show_operateStatus(operate_list);
            }
        });
    }

    @Override
    public void show_detailcontrolList(int groupId, String name) {
        getGroupList.show_detailcontrolList(groupId, name, new GetGroupList.Onresponse_1() {
            @Override
            public void onresult(List<CommonBean.GroupDetail> group_detail_list) {
                mView.show_detailcontrolList(group_detail_list);
            }
        });
    }

    @Override
    public void show_scenecontroller(int id) {
        getSceneList.show_scenecontroller(id, new GetSceneList.Onresponse_1() {
            @Override
            public void onresult(List<Map> scene_detail_show_list) {
                mView.scene_detail_show_list(scene_detail_show_list);
            }
        });
    }
}

