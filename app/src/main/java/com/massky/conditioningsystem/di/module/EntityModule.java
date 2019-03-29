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
package com.massky.conditioningsystem.di.module;

import com.massky.conditioningsystem.sql.CommonBean;
import javax.inject.Named;
import dagger.Module;
import dagger.Provides;

/**
 * @author: sunjian
 * created on: 2017/9/19 下午5:11
 * description: https://github.com/crazysunj/CrazyDaily
 */
@Module
public class EntityModule {

    public static final String NAME_COMMONBEAN = "CommonBean";
    public static final String NAME_COMMONBEAN_CONTROLLER = "CommonBean_Controller";
    public static final String NAME_COMMONBEAN_SCENE = "CommonBean_Scene";
    public static final String NAME_COMMONBEAN_GROUP = "CommonBean_Group";
    public static final String NAME_COMMONBEAN_OPERATE = "CommonBean_Operate";


    @Named(NAME_COMMONBEAN)
    @Provides
    CommonBean.Count providerCommonBean() {
        return new CommonBean.Count();
    }

    @Named(NAME_COMMONBEAN_CONTROLLER)
    @Provides
    CommonBean.controller providerCommonBeanController() {
        return new CommonBean.controller();
    }


    //CommonBean.scene
    @Named(NAME_COMMONBEAN_SCENE)
    @Provides
    CommonBean.scene providerCommonBeanScene() {
        return new CommonBean.scene();
    }

    @Named(NAME_COMMONBEAN_GROUP)
    @Provides
    CommonBean.group providerCommonBeanGroup() {
        return new CommonBean.group();
    }


    @Named(NAME_COMMONBEAN_OPERATE)
    @Provides
    CommonBean.operate providerCommonBeanOperate() {
        return new CommonBean.operate();
    }


}
