/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import pixyel_backend.database.exceptions.DbConnectionException;

/**
 *
 * @author Yannick
 */
public class DbConnection {

    private Connection con;

    public DbConnection() throws DbConnectionException {
        this.con = MysqlConnector.CONNECTION;
    }

    public DbConnection(boolean connectToTestDb) throws DbConnectionException {              
        if (connectToTestDb) {
            this.con = MysqlConnector.TESTCONNECTION;
        } 
    }

    public Connection getConnection() {
        return this.con;
    }

    public Statement getStatenement() throws SQLException {
        return con.createStatement();
    }

    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return this.con.prepareStatement(sql);
    }
}
