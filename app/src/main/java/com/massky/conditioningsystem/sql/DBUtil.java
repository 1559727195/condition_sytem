package com.massky.conditioningsystem.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static Connection getSQLConnection(String ip, String user, String pwd, String db)
    {
        Connection con = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            try {
//                con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + ip + ":1433/" + db + ";charset=utf8", user, pwd);
                con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + ip + ":1433/" + db , user, pwd);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return con;
    }

    public static String QuerySQL()
    {
        String result = "";
        try
        {
            Connection conn = getSQLConnection("192.168.169.210", "sa", "", "ackpass");
            String sql = "select top 10 * from [user]";
            Statement stmt = conn.createStatement();//
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                String s1 = rs.getString("username");
                String s2 = rs.getString("password");
                result += s1 + "  -  " + s2 + "\n";
                System.out.println(s1 + "  -  " + s2);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
            result += "查询数据异常!" + e.getMessage();
        }
        return result;
    }

    public static void main(String[] args)
    {
        QuerySQL();
    }

}
