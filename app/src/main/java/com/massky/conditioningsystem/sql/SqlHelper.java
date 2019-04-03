package com.massky.conditioningsystem.sql;

import java.lang.reflect.Field;

public class SqlHelper {
    /**
     * 首页获取：编写连表查询获取设备控制，场景控制，分组控制数据个数
     */
    public static String sqlCount = "   select sum(t.num1) as deviceCount,sum(t.num2) as sceneCount,sum(t.num3) as controlCount from (\n" +
            "    select count(*) num1,0 as num2,0 as num3 from [CenterControl].[dbo].[controller]\n" +
            "  union \n" +
            "    select 0 as num1,count(*) num2,0 as num3 from [CenterControl].[dbo].[scene]\n" +
            "\tunion \n" +
            "    select 0 as num1,0 as  num2,count(*) num3 from [CenterControl].[dbo].[group]\n" +
            ") t";


    /* 长按分组控制sql语句:*/
    public static String sqlgroupLongCLick = "select control.name from [CenterControl].[dbo].[controller] control where control.id =ANY\n" +
            "            (select [controllerID]  from [CenterControl].[dbo].[groupcontroller]  groupc\n" +
            "                    where groupc.groupID =";//3)";//3为group长按分组的组id,

    /**
     * 控制表operate
     */
    public static String sqlcontrol = "INSERT INTO [CenterControl].[dbo].[operate] (";
    public static String sqlcontrol_ip = "(SELECT com.ip FROM [CenterControl].[dbo].[communicator] as com where com.id =";
    public static String selectMaxid = " select SCOPE_IDENTITY() as id";

    /*
     *场景长按操作
     */
    public static String sqlsceneLongCLick_one_air = "select control.id, control.name,control.type,scene.power,scene.temperatureSet,scene.mode,scene.wind from [CenterControl].[dbo].[controller] control,\n" +
            "[CenterControl].[dbo].[scenecontroller] scene \n" +
            "where  control.id = ANY\n" +
            "( select [controllerID]  from [CenterControl].[dbo].[scenecontroller]  scenec \n" +
            " where scenec.sceneID = ";
    public static String sqlsceneLongCLick_two = ") and scene.controllerID = control.id  and scene.sceneID =";

    public static String sqlcontorller_mohu = "select * from [CenterControl].[dbo].[controller]  where name like ";//213%'
    public static String sqlscene_mohu = "select * from [CenterControl].[dbo].[scene]  where name like ";
    public static String sqlgroup_mohu = "select * from [CenterControl].[dbo].[group]  where name like ";

    public  static String sqlorderby = " order by control.name";//分组


    /**
     * @param o 操作对象
     * @param c 操作类。用于获取类中的方法
     * @return
     * @MethodName : getString
     * @Description : 获取类中全部属性及属性值
     */
    public static String getString(String name, Object o, Class<?> c) {
        String result = "";
//        String name = SqlHelper.sqlcontrol;
        // 获取父类。推断是否为实体类
//        if (c.getSuperclass().getName().indexOf("entity") >= 0) {
//            result += "\n<" + getString(o, c.getSuperclass()) + ">,\n";
//        }
        // 获取类中的全部定义字段
        Field[] fields = c.getDeclaredFields();

        for (int i = 0; i < fields.length - 2; i++) {
            fields[i].setAccessible(true);
            Class<?> type = fields[i].getType();
            //填坑
            try {
                if (fields[i].get(o) != null) {
                    if (("int".equals(type.getName()) || "java.lang.Long".equals(type.getName())) && (int) fields[i].get(o) == -1)
                        continue;
                    String fieldName = fields[i].getName();
                    if (fieldName.equals("id")) {
                        continue;
                    }

                    name += "[" + fieldName + "],";
                    switch (type.getName()) {
                        case "java.lang.String"://字段为string类型时
                            String content = (String) fields[i].get(o);
                            if (content.contains("SELECT")) {
                                result += fields[i].get(o) + ",";
                            } else {
                                result += "'" + fields[i].get(o) + "',";
                            }
                            break;
                        default:
                            result += fields[i].get(o) + ",";
                            break;
                    }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();

            }
        }
        if (result.indexOf(",") >= 0) result = result.substring(0, result.length() - 1);
        if (name.indexOf(",") >= 0) name = name.substring(0, name.length() - 1);
        name += ") SELECT ";
        name += result;
        return name;
    }
/*    }
    operate: address = 1,
    flag = 0,
    id = -1,
    ip = 1),
    mode = 制热,
    power = 打开,
    status = 1,
    temperatureSet = 22,
    wind = 中风,
    $change = null,
    serialVersionUID = -4349780701152742047,*/

    /**
     * 模糊查询语句
     *
     * @param str
     * @return
     */

    public static String sqlencode(String str) {
        return str.replace("[", "[[]")//'此句一定要在最前
                .replace("_", "[_]")
                .replace("%", "[%]");
    }

}
