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
package com.massky.conditioningsystem.presenter.contract;

import com.massky.conditioningsystem.base.IPresenter;
import com.massky.conditioningsystem.base.IView;
import com.massky.conditioningsystem.sql.CommonBean;

import java.util.List;
import java.util.Map;

/**
 * @author: sunjian
 * created on: 2017/9/19 下午5:05
 * description: https://github.com/crazysunj/CrazyDaily
 */
public interface HomeContract {

    interface View extends IView {
        void showsqlCounts(List<Map> list_dsc_count);

        void show_deviceList(List<Map> controller_show_list, List<CommonBean.controller> controller_list);

        void show_sceneList(List<Map> scene_show_list, List<CommonBean.scene> scene_list);

        void show_groupList(List<Map> group_show_list, List<CommonBean.group> group_list);

        void show_operate_max_id(long operate_max_id);

        void show_operateStatus(List<CommonBean.operate> operate_list);

        void show_detailcontrolList(List<CommonBean.GroupDetail> group_detail_list);

        void scene_detail_show_list(List<Map> scene_detail_show_list);
    }

    interface Presenter extends IPresenter<View> {
        void getSqlCounts();

        void show_deviceList(String trim);

        void show_sceneList(String trim);

        void show_controlList(String trim);

        void show_control_device(final String sql, final CommonBean.operate operate, final String selectMaxId);

        void show_operateStatus(long operate_max_id);

        void show_detailcontrolList(final int groupId, final String name);

        void show_scenecontroller(int id);
    }
}
