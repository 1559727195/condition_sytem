package com.massky.conditioningsystem.sql;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//基于泛型类
public class BaseDao<T> {
    Class<T> clazz;

    public BaseDao() {
        Type type = this.getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        clazz = (Class<T>) types[0];
    }


    //查询表带参数
    public List<T> queryList(T t, final onresponse onresponse) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = getQuerySql(t);
        List<T> list = new ArrayList<T>();
        try {
            conn = DBUtilNew.getConn(onresponse);
            ps = conn.prepareStatement(sql);
            common_excute(t, ps);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            while (rs.next()) {
                T obj = (T) clazz.newInstance();
                for (int i = 0; i < count; i++) {
                    String fieldName = metaData.getColumnName(i + 1);
                    Field field = clazz.getDeclaredField(fieldName);
                    Method method = clazz.getMethod(getSetter(fieldName), field.getType());
                    Class<?> type1 = field.getType();
                    String name = type1.getName();
                    Object object = rs.getObject(i + 1);
                    if (object == null) {
                        //
                        Class<?> type = field.getType();
                        //填坑
                        switch (type.getName()) {
                            case "int":
                            case "java.lang.Float":
                            case "java.lang.Long":
                                method.invoke(obj, 0);
                                break;
                            case "java.lang.Boolean":
                                method.invoke(obj, false);
                                break;
                            case "java.lang.String":
                                method.invoke(obj, "");
                                break;
                        }
                    } else {
                        method.invoke(obj, object);
                    }
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(rs, ps, conn);
        }
        return list;
    }


    //查询表带参数
    public List<T> querySqlList(T t, String sql, onresponse onresponse) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
//        String sql = getQuerySql(t);
        List<T> list = new ArrayList<T>();
        try {
            conn = DBUtilNew.getConn(onresponse);
            ps = conn.prepareStatement(sql);
//            common_excute(t, ps);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            while (rs.next()) {
                T obj = (T) clazz.newInstance();
                for (int i = 0; i < count; i++) {
                    String fieldName = metaData.getColumnName(i + 1);
                    Field field = clazz.getDeclaredField(fieldName);
                    Method method = clazz.getMethod(getSetter(fieldName), field.getType());
                    method.invoke(obj, rs.getObject(i + 1));
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(rs, ps, conn);
        }
        return list;
    }

    //插入SQL参数
    public Object insertSqlList(T t, String sql, onresponse onresponse) {
        Connection conn = null;
        PreparedStatement ps = null;
        Object object = null;
//        String sql = getInsertSql(t);
        try {
            conn = DBUtilNew.getConn(onresponse);
            ps = conn.prepareStatement(sql);
            ResultSet rs =   ps.executeQuery();
            while (rs.next()) {
                object = rs.getObject(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(ps, conn);
        }
        return object;
    }


    //查询表带参数
    public void deleteList(T t) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = getDeleteSql(t);
        try {
            conn = DBUtilNew.getConn(null);
            ps = conn.prepareStatement(sql);
            common_excute(t, ps);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(ps, conn);
        }
    }

    /**
     * 共同执行方法
     *
     * @param t
     * @param ps
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws SQLException
     */
    private void common_excute(T t, PreparedStatement ps) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLException {
        Field[] fields = clazz.getDeclaredFields();
        int c = 1;
        for (int i = 0; i < fields.length - 2; i++) {
            fields[i].setAccessible(true);
            Class<?> type = fields[i].getType();
            //填坑
            if (fields[i].get(t) != null) {
                if (("int".equals(type.getName()) || "java.lang.Long".equals(type.getName())) && (int) fields[i].get(t) == -1)
                    continue;
                String fieldName = fields[i].getName();
                Method method = clazz.getMethod(getGetter(fieldName));
                Object obj = method.invoke(t);
                ps.setObject(c, obj);
                fields[i].setAccessible(false);
                c++;
            }
        }
    }

    /**
     *  
     *      * 增加、删除、改 
     *      * @param sql 
     *      * @param params 
     *      * @return 
     *      * @throws SQLException 
     *      
     */
    public boolean updateByPreparedStatement(String sql, List<Object> params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean flag = false;
        int result = -1;
        try {
            conn = DBUtilNew.getConn(null);
            ps = conn.prepareStatement(sql);
            int index = 1;
            if (params != null && !params.isEmpty()) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(index++, params.get(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(ps, conn);
        }

        result = ps.executeUpdate();
        flag = result > 0 ? true : false;
        return flag;
    }


    //查询单个po
    public T queryPo(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        T t = null;
        String sql = "select * from " + clazz.getSimpleName() + " where  id=?";
        try {
            t = (T) clazz.newInstance();
            conn = DBUtilNew.getConn(null);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 0; i < count; i++) {
                    String fieldName = metaData.getColumnName(i + 1);
                    Field filed = clazz.getDeclaredField(fieldName);
                    Method method = clazz.getMethod(getSetter(fieldName), filed.getType());
                    method.invoke(t, rs.getObject(i + 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(rs, ps, conn);
        }
        return t;
    }

    //获取符合条件的po 个数
    public int queryCount(T t) {
        return queryList(t, null).size();
    }

    //添加po
    public void insertList(T t) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = getInsertSql(t);
        try {
            conn = DBUtilNew.getConn(null);
            ps = conn.prepareStatement(sql);
            common_excute(t, ps);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(ps, conn);
        }
    }

//修改po

    /**
     * 更新泛型list
     *
     * @param t
     */
    public void updateList(T t, Map map) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = getUpdateSql(t, map);
        try {
            conn = DBUtilNew.getConn(null);
            ps = conn.prepareStatement(sql);
            Field[] fields = clazz.getDeclaredFields();
            int c = 1;
            for (int i = 0; i < fields.length - 2; i++) {
                fields[i].setAccessible(true);
                Class<?> type = fields[i].getType();
                //填坑
                if (fields[i].get(t) != null) {
                    if (("int".equals(type.getName()) || "java.lang.Long".equals(type.getName())) && (int) fields[i].get(t) == -1)
                        continue;
                    String fieldName = fields[i].getName();
                    Method method = clazz.getMethod(getGetter(fieldName));
                    Object obj = method.invoke(t);
                    ps.setObject(c, obj);
                    fields[i].setAccessible(false);
                    c++;//set之后where之前的字段
                }
            }

            Set<String> keys = map.keySet();
            for (String key : keys) {
                ps.setObject(c, map.get(key));
                c++;//where and之间的字段
            }
//            ps.setInt(c, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(ps, conn);
        }
    }

    //删除
    public void deletePo(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "delete from " + clazz.getSimpleName() + "  where id=?";
        System.out.println(sql);
        try {
            conn = DBUtilNew.getConn(null);
            ps = conn.prepareStatement(sql);
//            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtilNew.close(ps, conn);
        }
    }

    //获取查询的sql语句
    public String getQuerySql(T t) {
        String sql = "select * from " + "[" + clazz.getSimpleName() + "]";
        sql = get_common_sql(t, sql);
        return sql;
    }

    //获取查询的sql语句
    public String getDeleteSql(T t) {
        String sql = "delete from  " + "[" + clazz.getSimpleName() + "]";
        sql = get_common_sql(t, sql);
        return sql;
    }


    /**
     * 公共拾起来语句集合
     *
     * @param t
     * @param sql
     * @return
     */
    private String get_common_sql(T t, String sql) {
        try {
            sql += " where 1=1";
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length - 2; i++) {
                fields[i].setAccessible(true);
                Class<?> type = fields[i].getType();
                if (fields[i].get(t) != null) {
                    if (("int".equals(type.getName()) || "java.lang.Long".equals(type.getName())) && (int) fields[i].get(t) == -1)
                        continue;
                    sql += " and " + fields[i].getName() + "=?";
                }
            }
            sql = sql.substring(0, sql.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }

    //获取修改的sql语句
    public String getUpdateSql(T t, Map map) {
        String sql = "update " + "[" + clazz.getSimpleName() + "]" + " set ";
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (int i = 0; i < fields.length - 2; i++) {
                fields[i].setAccessible(true);
                Class<?> type = fields[i].getType();
                if (fields[i].get(t) != null) {
                    if (("int".equals(type.getName()) || "java.lang.Long".equals(type.getName())) && (int) fields[i].get(t) == -1)
                        continue;
                    sql += fields[i].getName() + "=?,";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sql = sql.substring(0, sql.length() - 1) + " where 1 = 1";
        //and " + fields[i].getName() + "=?"
        Set<String> keys = map.keySet();
        for (String key : keys) {
            sql += " and " + key + "=?";
        }
        return sql;
    }


    //获取添加的sql语句
    public String getInsertSql(T t) {
        String sql = "insert into " + "[" + clazz.getSimpleName() + "]" + "(";
        String param = "";
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (int i = 0; i < fields.length - 2; i++) {
                fields[i].setAccessible(true);

                Class<?> type = fields[i].getType();
                if (fields[i].get(t) != null) {
                    if (("int".equals(type.getName()) || "java.lang.Long".equals(type.getName())) && (int) fields[i].get(t) == -1)
                        continue;
                    sql += fields[i].getName() + ",";
                    param += "?,";
                }
            }
            sql = sql.substring(0, sql.length() - 1) + ") values(" + param;
            sql = sql.substring(0, sql.length() - 1) + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }

    //获取set方法的方法名
    public static String getSetter(String fieldName) {
//传入属性名 拼接set方法  
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    //获取get方法的方法名
    public static String getGetter(String fieldName) {
//传入属性名 拼接set方法  
        String temp;
        temp = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return temp;
    }

    public interface onresponse {
        void onresponse(String content);
    }
}
