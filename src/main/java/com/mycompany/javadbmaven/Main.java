package com.mycompany.javadbmaven;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import org.sqlite.SQLiteConfig;

public class Main {
    public static Connection connection;
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String dbName = "Cutest.db"; /* Insert your database name */
        SQLiteConfig conf = new SQLiteConfig();
        conf.enforceForeignKeys(true);
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbName,conf.toProperties());
        createDB(); /* Creating database, might be commented */
        System.out.println("Database successfully loaded");
        Scanner sc = new Scanner(System.in);
        String userCommand;
        /*
            Type "quit" to close application.
            Command format: TableName Action Parameters
        */
        while ((userCommand = sc.nextLine()).compareTo("quit") != 0)
        {
            String[] userCommandParsed = userCommand.split("\\s+");
            executeQuery(userCommandParsed);
        }
    }

    public static void createDB () throws SQLException
    {
        Statement s = connection.createStatement();
        s.execute("PRAGMA foreign_keys = ON");
        s.execute("create table if not exists [Customers] "
                + "([C_ID] INT PRIMARY KEY, [CustomerName] NVARCHAR(50));");
        s.execute("create table if not exists [Orders] "
                + "([O_ID] INT PRIMARY KEY, [C_ID] INT, "
                + "FOREIGN KEY(C_ID) REFERENCES Customers(C_ID) "
                + "ON DELETE CASCADE ON UPDATE CASCADE);");
        s.execute("create table if not exists [Goods] "
                + "([G_ID] INT PRIMARY KEY, [GoodsName] NVARCHAR(50));");
        s.execute("create table if not exists [OrdersGoods] "
                + "([OG_ID] INT PRIMARY KEY, [O_ID] INT, [G_ID] INT, FOREIGN KEY(O_ID) "
                + "REFERENCES Orders(O_ID) ON DELETE CASCADE ON UPDATE CASCADE, "
                + "FOREIGN KEY(G_ID) REFERENCES Goods(G_ID) ON DELETE CASCADE ON UPDATE CASCADE);");
        System.out.println("Database successfully crateted");
    }

    public static void executeQuery(String[] args) throws SQLException{
        switch (args[1]){
            case "create":
                executeCreate(args);
                break;
            case "update":
                executeUpdate(args);
                break;
            case "delete":
                executeDelete(args);
                break;
            case "read":
                executeRead(args);
                break;
            default:
                System.out.println("No such command");
                break;
        }
    }

    public static void executeRead(String[] args)throws SQLException{
        Statement s = connection.createStatement();
        ResultSet results;
        ResultSetMetaData metaData;
        int noCols;
        switch (args[0]){
            case "Customers":
                results = s.executeQuery("select * from Customers");
                metaData = results.getMetaData();
                noCols = metaData.getColumnCount();
                System.out.printf("%-10s\t", "CustomerID");
                System.out.printf("%-10s\t", "CustomerName");
                System.out.println();
                while (results.next())
                {
                    for (int i = 1; i <= noCols; i++)
                    {
                        System.out.printf("%-10s\t", results.getObject(i));
                    }
                    System.out.println();
                }
                break;
            case "Goods":
                results = s.executeQuery("select * from Goods");
                metaData = results.getMetaData();
                noCols = metaData.getColumnCount();
                System.out.printf("%-10s\t", "ProductID");
                System.out.printf("%-10s\t", "ProductName");
                System.out.println();
                while (results.next())
                {
                    for (int i = 1; i <= noCols; i++)
                    {
                        System.out.printf("%-10s\t", results.getObject(i));
                    }
                    System.out.println();
                }
                break;
            case "Orders":
                results = s.executeQuery("select O.O_ID, C.CustomerName "
                        + "from orders as O inner join customers as C on C.C_ID=O.C_ID");
                metaData = results.getMetaData();
                noCols = metaData.getColumnCount();
                System.out.printf("%-10s\t", "OrderID");
                System.out.printf("%-10s\t", "Customer");
                System.out.println();
                while (results.next())
                {
                    for (int i = 1; i <= noCols; i++)
                    {
                        System.out.printf("%-10s\t", results.getObject(i));
                    }
                    System.out.println();
                }
                break;
            case "OrdersGoods":
                results = s.executeQuery("select C.CustomerName, G.GoodsName "
                        + "from ordersgoods as OG inner join goods as G on OG.G_ID=G.G_ID "
                        + "inner join orders as O on O.O_ID=OG.O_ID "
                        + "inner join customers as C on C.C_ID=O.C_ID");
                metaData = results.getMetaData();
                noCols = metaData.getColumnCount();
                System.out.printf("%-10s\t", "Customer");
                System.out.printf("%-10s\t", "ProductOrdered");
                System.out.println();
                while (results.next())
                {
                    for (int i = 1; i <= noCols; i++)
                    {
                        System.out.printf("%-10s\t", results.getObject(i));
                    }
                    System.out.println();
                }
                break;
            default:
                System.out.print("No such table");
                break;
        }
    }

    public static void executeCreate(String[] args)throws SQLException{
        PreparedStatement s;
        boolean f;
        switch (args[0]){
            case "Customers":
                s = connection.prepareStatement("insert into Customers values (?,?)");
                s.setInt(1,Integer.parseInt(args[2]));
                s.setString(2,args[3]);
                f = s.execute();
                if (!f) {
                    System.out.println("Customer added");
                }
                break;
            case "Goods":
                s = connection.prepareStatement("insert into Goods values (?,?)");
                s.setInt(1,Integer.parseInt(args[2]));
                s.setString(2, args[3]);
                f = s.execute();
                if (!f) {
                    System.out.println("Good added");
                }
                break;
            case "Orders":
                s = connection.prepareStatement("insert into Orders values (?,?)");
                s.setInt(1,Integer.parseInt(args[2]));
                s.setInt(2,Integer.parseInt(args[3]));
                f = s.execute();
                if (!f) {
                    System.out.println("Order added");
                }
                break;
            case "OrdersGoods":
                s = connection.prepareStatement("insert into OrdersGoods values (?,?,?)");
                s.setInt(1,Integer.parseInt(args[2]));
                s.setInt(2,Integer.parseInt(args[3]));
                s.setInt(3,Integer.parseInt(args[4]));
                f = s.execute();
                if (!f) {
                    System.out.println("OrdersGoods added");
                }
                break;
            default:
                System.out.println("No such table");
                break;
        }
    }

    public static void executeUpdate(String[] args)throws SQLException{
        PreparedStatement s;
        boolean f;
        switch (args[0]){
            case "Customers":
                s = connection.prepareStatement("update Customers set CustomerName=? where C_ID=?");
                s.setInt(2, Integer.parseInt(args[2]));
                s.setString(1, args[3]);
                f=s.execute();
                if (!f==true) {
                    System.out.println("Customer updated");
                }
                break;
            case "Goods":
                s = connection.prepareStatement("update Goods set GoodsName=? where G_ID=?");
                s.setInt(2, Integer.parseInt(args[2]));
                s.setString(1, args[3]);
                f=s.execute();
                if (!f) {
                    System.out.println("Good updated");
                }
                break;
            case "Orders":
                s = connection.prepareStatement("update Orders set C_ID=? where O_ID=?");
                s.setInt(1, Integer.parseInt(args[3]));
                s.setInt(2, Integer.parseInt(args[2]));
                f=s.execute();
                if (!f) {
                    System.out.println("Order update");
                }
                break;
            case "OrdersGoods":
                s = connection.prepareStatement("update OrdersGoods set O_ID=?, G_ID=? where OG_ID=?");
                s.setInt(1, Integer.parseInt(args[3]));
                s.setInt(2, Integer.parseInt(args[4]));
                s.setInt(3, Integer.parseInt(args[2]));
                f = s.execute();
                if (!f)
                    System.out.println("OrdersGoods updated");
                break;
            default:
                System.out.println("No such table");
                break;
        }
    }

    public static void executeDelete(String[] args)throws SQLException{
        PreparedStatement s;
        boolean f;
        switch (args[0]){
            case "Customers":
                s = connection.prepareStatement("delete from Customers where C_ID=?");
                s.setInt(1, Integer.parseInt(args[2]));
                f = s.execute();
                if (!f)
                    System.out.println("Customer deleted");
                break;
            case "Goods":
                s = connection.prepareStatement("delete from Goods where G_ID=?");
                s.setInt(1, Integer.parseInt(args[2]));
                f = s.execute();
                if (!f)
                    System.out.println("Good deleted");
                break;
            case "Orders":
                s = connection.prepareStatement("delete from Orders where O_ID=?");
                s.setInt(1, Integer.parseInt(args[2]));
                f = s.execute();
                if (!f)
                    System.out.println("Order deleted");
                break;
            case "OrdersGoods":
                s = connection.prepareStatement("delete from OrdersGoods where OG_ID=?");
                s.setInt(1, Integer.parseInt(args[2]));
                f = s.execute();
                if (!f)
                    System.out.println("OrdersGoods deleted");
                break;
            default:
                System.out.println("No such table");
                break;
        }
    }
}