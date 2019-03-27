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
    }

    interface Presenter extends IPresenter<View> {
      void  getSqlCounts();
      void show_deviceList();
    }
}
