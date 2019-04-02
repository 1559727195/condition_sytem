package com.massky.conditioningsystem.sql;

import java.io.Serializable;
import java.util.List;

public class CommonBean {
    public List<userlist> getUserList() {
        return userList;
    }

    public void setUserList(List<userlist> userList) {
        this.userList = userList;
    }

    public List<userlist> userList;

    public static class userlist extends BaseDao<userlist> implements Serializable {
        private int id = -1;//整数默认值都为-1
        public String pass;

        public void setId(int id) {
            this.id = id;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getPass() {
            return pass;
        }

        public String getName() {
            return name;
        }

        public String name;
    }


    /*  1.控制主机表
      表名：communicator
      字段	类型	说明
      id	bigint	自增字段
      name	varchar(50)	主机名
      ip	varchar(50)	主机IP地址
      status	varchar(50)	状态值，分为在线和断线
      type	varchar(50)	类型，分为空调和新风
      px	int	地图X坐标
      py	int	地图Y坐标
      mapID	int	地图编号*/
    public List<communicator> communicatorList;

    public static class communicator extends BaseDao<communicator> implements Serializable {
        //整数默认值都为-1
        public int id = -1;
        public String name;
        public String ip;
        public String status;
        public String type;
        public int px = -1;
        public int py = -1;
        public int mapID = -1;
    }


  /*  2.控制设备表
    表名：controller
    字段	类型	说明
    id	bigint	自增字段
    name	varchar(50)	设备名
    address	int	设备物理地址
    status	varchar(50)	状态值，分为在线和断线
    type	varchar(50)	类型，分为空调和新风
    communicatorID	int	主机编号，表示对应哪个主机
    px	int	地图X坐标
    py	int	地图Y坐标
    mapID	int	地图编号
    power	varchar(10)	电源开关，分为打开和关闭
    temperature	int	当前温度
    temperatureSet	int	设定温度
    mode	varchar(10)	模式
    wind	varchar(10)	风速*/

    public static class controller extends BaseDao<controller> implements Serializable {
        public int id = -1;
        public String name;
        public int address = -1;
        public String status;
        public String type;
        public int communicatorID = -1;
        public int px = -1;
        public int py = -1;
        public int mapID = -1;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAddress() {
            return address;
        }

        public String getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

        public int getCommunicatorID() {
            return communicatorID;
        }

        public int getPx() {
            return px;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(int address) {
            this.address = address;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setCommunicatorID(int communicatorID) {
            this.communicatorID = communicatorID;
        }

        public void setPx(int px) {
            this.px = px;
        }

        public void setPy(int py) {
            this.py = py;
        }

        public void setMapID(int mapID) {
            this.mapID = mapID;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public void setTemperatureSet(int temperatureSet) {
            this.temperatureSet = temperatureSet;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public int getPy() {
            return py;
        }

        public int getMapID() {
            return mapID;
        }

        public String getPower() {
            return power;
        }

        public int getTemperature() {
            return temperature;
        }

        public int getTemperatureSet() {
            return temperatureSet;
        }

        public String getMode() {
            return mode;
        }

        public String getWind() {
            return wind;
        }

        public String power;
        public int temperature = -1;
        public int temperatureSet = -1;
        public String mode;
        public String wind;
    }

/*    3.控制设备分组表
    表名：group
    字段	类型	说明
    id	bigint	自增字段
    name	varchar(50)	分组名
    type	varchar(10)	类型，分为空调组和新风组
    px	int	地图X坐标
    py	int	地图Y坐标*/

    public static class group extends BaseDao<group> implements Serializable {
        public int id = -1;
        public String name;
        public String type;
        public int px = -1;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setPx(int px) {
            this.px = px;
        }

        public void setPy(int py) {
            this.py = py;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public int getPx() {
            return px;
        }

        public int getPy() {
            return py;
        }

        public int py = -1;
    }

    /**
     * 分组详情列表显示
     */
    public static class GroupDetail extends BaseDao<GroupDetail> implements Serializable {
        public String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    /*  4.分组设备关联表
      表名：groupcontroller
      字段	类型	说明
      groupID	int	组编号
      controllerID	int	设备编号*/
    public static class groupcontroller extends BaseDao<groupcontroller> implements Serializable {
        public int groupID = -1;
        public int controllerID = -1;
    }

    /*5.场景表
    表名：scene
    字段	类型	说明
    id	bigint	自增字段
    name	varchar(50)	场景名
    type	int	类型，0-手动，1-自动，2-定时
    flag	int	启用标记，自动和定时场景时使用，1-启用，0-不启用
    startTime	varchar(50)	自动场景时为有效开始时间，定时场景时为执行时间
    endTime	varchar(50)	自动场景时为有效结束时间
    dt1	int	星期一，定时场景时使用，1-有效，0-无效
    dt2	int	星期二，定时场景时使用，1-有效，0-无效
    dt3	int	星期三，定时场景时使用，1-有效，0-无效
    dt4	int	星期四，定时场景时使用，1-有效，0-无效
    dt5	int	星期五，定时场景时使用，1-有效，0-无效
    dt6	int	星期六，定时场景时使用，1-有效，0-无效
    dt7	int	星期日，定时场景时使用，1-有效，0-无效
    px	int	地图X坐标
    py	int	地图Y坐标*/

    public static class scene extends BaseDao<scene> implements Serializable {

        public int id = -1;
        public String name;
        public int type = -1;
        public int flag = -1;
        public String startTime;
        public String endTime;
        public int dt1 = -1;
        public int dt2 = -1;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public void setDt1(int dt1) {
            this.dt1 = dt1;
        }

        public void setDt2(int dt2) {
            this.dt2 = dt2;
        }

        public void setDt3(int dt3) {
            this.dt3 = dt3;
        }

        public void setDt4(int dt4) {
            this.dt4 = dt4;
        }

        public void setDt5(int dt5) {
            this.dt5 = dt5;
        }

        public void setDt6(int dt6) {
            this.dt6 = dt6;
        }

        public void setDt7(int dt7) {
            this.dt7 = dt7;
        }

        public void setPx(int px) {
            this.px = px;
        }

        public void setPy(int py) {
            this.py = py;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

        public int getFlag() {
            return flag;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public int getDt1() {
            return dt1;
        }

        public int getDt2() {
            return dt2;
        }

        public int getDt3() {
            return dt3;
        }

        public int getDt4() {
            return dt4;
        }

        public int getDt5() {
            return dt5;
        }

        public int getDt6() {
            return dt6;
        }

        public int getDt7() {
            return dt7;
        }

        public int getPx() {
            return px;
        }

        public int getPy() {
            return py;
        }

        public int dt3 = -1;
        public int dt4 = -1;
        public int dt5 = -1;
        public int dt6 = -1;
        public int dt7 = -1;
        public int px = -1;
        public int py = -1;
    }

    /*    6.场景动作表
        表名：scenecontroller
        字段	类型	说明
        sceneID	int	场景编号
        controllerID	int	设备编号
        power	varchar(10)	电源开关，分为打开和关闭
        temperatureSet	int	设定温度
        mode	varchar(10)	模式
        wind	varchar(10)	风速*/
    public static class scenecontroller extends BaseDao<scenecontroller> implements Serializable {
        public int sceneID = -1;
        public int controllerID = -1;
        public String power;
        public int temperatureSet = -1;
        public String mode;
        public String wind;

        public void setSceneID(int sceneID) {
            this.sceneID = sceneID;
        }

        public void setControllerID(int controllerID) {
            this.controllerID = controllerID;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public void setTemperatureSet(int temperatureSet) {
            this.temperatureSet = temperatureSet;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public int getSceneID() {
            return sceneID;
        }

        public int getControllerID() {
            return controllerID;
        }

        public String getPower() {
            return power;
        }

        public int getTemperatureSet() {
            return temperatureSet;
        }

        public String getMode() {
            return mode;
        }

        public String getWind() {
            return wind;
        }
    }

    /*    控制操作表结构定义
        表名：operate
        字段	类型	说明
        id	bigint	自增字段
        status	int	状态值
        插入的命令字定义：
                1-空调控制，2-新风控制，3-空调组控制，4-新风组控制，5-场景控制
        返回值定义：
                100-成功，101-失败
        flag	int	插入数据状态
    0-控制操作，1-自动操作
        ip	varchar(50)	控制主机IP
        status=1、2，使用
                status=3、4、5，不使用
        address	int	控制设备物理地址或是其他编号
        status=1、2，为控制设备实际物理地址
                status=3、4、5，为组编号或是场景编号
        power	varchar(10)	开关，分为打开和关闭
                status=1、2、3、4，为打开、关闭
                status=5，为打开
        temperatureSet	int	温度
        status=1、3，使用
                status=2、4、5，不使用
        mode	varchar(10)	模式，分为制冷、制热、通风、自动
                status=1、3，使用
                status=2、4、5，不使用
        wind	varchar(10)	风速，分为低风、中风、高风、自动
                status=1、3，使用
                status=2、4、5，不使用
        */
    public static class operate extends BaseDao<operate> implements Serializable {

        public int flag = -1;
        public String ip;
        public int address = -1;
        public String power;

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setAddress(int address) {
            this.address = address;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public void setTemperatureSet(int temperatureSet) {
            this.temperatureSet = temperatureSet;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getIp() {
            return ip;
        }

        public int getAddress() {
            return address;
        }

        public String getPower() {
            return power;
        }

        public int getTemperatureSet() {
            return temperatureSet;
        }

        public String getMode() {
            return mode;
        }

        public String getWind() {
            return wind;
        }

        public long getId() {
            return id;
        }

        public int getStatus() {
            return status;
        }

        public int temperatureSet = -1;
        public String mode;
        public String wind;
        public long id = -1;
        public int status = -1;
    }

    /**
     * 获取设备，场景，组控侧栏数据
     */
    public static class Count extends BaseDao<Count> implements Serializable {
        public int deviceCount = -1;
        public int sceneCount = -1;
        public int controlCount = -1;

        public void setDeviceCount(int deviceCount) {
            this.deviceCount = deviceCount;
        }

        public void setSceneCount(int sceneCount) {
            this.sceneCount = sceneCount;
        }

        public void setControlCount(int controlCount) {
            this.controlCount = controlCount;
        }

        public int getDeviceCount() {
            return deviceCount;
        }

        public int getSceneCount() {
            return sceneCount;
        }

        public int getControlCount() {
            return controlCount;
        }
    }


    /**
     * 场景长按动作查询表
     */
    public static class sceneDetail extends BaseDao<sceneDetail> implements Serializable {
        public int id = -1;
        public String name;
        public String type;
        public String power;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public void setTemperatureSet(int temperatureSet) {
            this.temperatureSet = temperatureSet;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getPower() {
            return power;
        }

        public int getTemperatureSet() {
            return temperatureSet;
        }

        public String getMode() {
            return mode;
        }

        public String getWind() {
            return wind;
        }

        public int temperatureSet = -1;
        public String mode;
        public String wind;
    }


}
