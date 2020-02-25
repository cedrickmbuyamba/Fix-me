package org.snitchers.fix_me.utilities;

import java.sql.*;

public class DB_Handler {
    private Statement statement;
    private PreparedStatement preparedStatement;
    public static int id;
    private String fileName;
    private Connection connection;

    private String CREATE_CLIENTS = "CREATE TABLE IF NOT EXISTS clients" +
            "(CLIENT_ID INTEGER NOT NULL PRIMARY KEY," +
            "FUNDS INTEGER NOT NULL," +
            "CREATED CURRENT_TIMESTAMP )";

    private String CREATE_FIX_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS fixMessages"+
            "(MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "BROKER_ID INTEGER NOT NULL," +
            "FIX_MESSAGE VARCHAR(255) NOT NULL)";

    private String INSERT_CLIENT = "INSERT INTO clients (CLIENT_ID, funds) VALUES (?,?)";

    private String INSERT_FIX_MESSAGES = "INSERT INTO fixMessages (BROKER_ID, FIX_MESSAGE) VALUES (?,?)";

    private String GET_LAST_ID = "SELECT * FROM clients ORDER BY CLIENT_ID DESC LIMIT 1";

    private String VALIDATE_ID = "SELECT COUNT(*) AS rowNum FROM clients WHERE CLIENT_ID = ?";

    public DB_Handler() {
        fileName = "jdbc:sqlite:fixMe.db";
        try
        {
            String driver = "org.sqlite.JDBC";
            Class.forName(driver);
            connection = getConnection();
            if (connection != null)
            {
                statement = connection.createStatement();
                statement.execute(CREATE_CLIENTS);
                statement.execute(CREATE_FIX_MESSAGE_TABLE);
                try {
                    ResultSet resultSet = statement.executeQuery(GET_LAST_ID);
                    id = resultSet.getInt("CLIENT_ID");
                } catch (SQLException e) {
                    id = 0;
                }
            }

            if (id < 100000){
                insertClient(100000, 100000);
                id = 100000;
            }
            System.out.println("Connection established...");
        } catch (SQLException e)
        {
            System.out.println("Error connecting to database: " + e.getMessage());
        } catch (ClassNotFoundException e)
        {
            System.out.println("Database class driver not found: " + e.getMessage());
        } finally {
            closeDB();
        }
    }

    public Connection getConnection() {
        try
        {
            System.out.println("Connecting to database...");
            return (DriverManager.getConnection(fileName));
        }catch (SQLException e)
        {
            System.out.println("Error creating connection: " + e.getMessage());
            return null;
        }
    }

    public void insertFixMessage(int clientID, String fixMessage) {
        try
        {
            assert connection != null;
            preparedStatement = connection.prepareStatement(INSERT_FIX_MESSAGES);
            preparedStatement.setInt(1, clientID);
            preparedStatement.setString(2, fixMessage);
            preparedStatement.execute();
            System.out.println("Messages well inserted...");
        }catch (SQLException e)
        {
            System.out.println("OOPS!!! Something went wrong while inserting: " + e.getMessage());
        }finally {
            closeDB();
        }
    }

    public void insertClient(int clientId, int funds) {
        try {
            assert connection != null;
            preparedStatement = connection.prepareStatement(INSERT_CLIENT);
            preparedStatement.setInt(1, clientId);
            preparedStatement.setInt(2, funds);
            preparedStatement.execute();
            System.out.println("Client added ...");
        } catch (SQLException e) {
            System.out.println("Error inserting broker" + e.getMessage());
        } finally {
            closeDB();
        }
    }

    public boolean checkClientId(int clientId){
        try{
            assert connection != null;
            preparedStatement = connection.prepareStatement(VALIDATE_ID);
            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            int id = resultSet.getInt("rowNum");
            if (id != 0)
                return true;
        } catch (SQLException e) {
            System.out.println("Error checking client id " + e.getMessage());
        } finally {
            closeDB();
        }
        return false;
    }

    private void closeDB() {
        try {
            if (statement != null && !statement.isClosed())
            {
                statement.close();
            }
            if (preparedStatement != null && !preparedStatement.isClosed())
            {
                preparedStatement.close();
            }
        }catch (SQLException e)
        {
            System.out.println("Failed to close connection: " + e.getMessage());
        }
    }
}
