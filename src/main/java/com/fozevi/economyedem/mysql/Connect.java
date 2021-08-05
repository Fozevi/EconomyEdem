package com.fozevi.economyedem.mysql;

import com.fozevi.economyedem.EconomyEdem;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Connect {

    public String host, database, username, password;
    public int port;

    EconomyEdem plugin = EconomyEdem.getInstance;

    private Connection connection;

    public void mysqlSetup() {
        host = plugin.getConfig().getString("mysql.host");
        port = plugin.getConfig().getInt("mysql.port");
        database = plugin.getConfig().getString("mysql.database");
        username = plugin.getConfig().getString("mysql.username");
        password = plugin.getConfig().getString("mysql.password");
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?"+"characterEncoding=utf8", this.username, this.password));
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS currencies(id integer primary key auto_increment, `currencyName` varchar(100), `creator` varchar(30), `owner1` varchar(30), `owner2` varchar(30), `owner3` varchar(30), `cost` integer) CHARACTER SET utf8 COLLATE utf8_general_ci;");
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS logs(id integer primary key auto_increment, `nickname1` varchar(30), `nickname2` varchar(30), `action` varchar(30), `value` integer, `currency1` varchar(30), `currency2` varchar(30), `cost2UP` integer, `cost1Down` integer) CHARACTER SET utf8 COLLATE utf8_general_ci;");
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS machines(id integer primary key auto_increment, `currency` varchar(30), `machine1` boolean not null default 0, `time1` integer default 60, `money1` integer default 100, `purchased1` boolean not null default 0, timeLvl integer not null default 1, moneyLvl integer not null default 1) CHARACTER SET utf8 COLLATE utf8_general_ci;");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public ArrayList<HashMap<String, Object>> getMachines() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM machines");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<HashMap<String, Object>> machines = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Object> machine = new HashMap<>();
                machine.put("currency", resultSet.getString("currency"));
                machine.put("machine1", resultSet.getBoolean("machine1"));
                machine.put("time1", resultSet.getInt("time1"));
                machine.put("money1", resultSet.getInt("money1"));
                machines.add(machine);
            }
            return machines;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }





    public Integer getMoneysInCurrency(String currency) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT money FROM " + currency);
            ResultSet resultSet = statement.executeQuery();
            Integer moneys = 0;
            while (resultSet.next()) {
                moneys += resultSet.getInt("money");
            }
            return moneys;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void addLog(String nickname1, String nickname2, String action, Integer value, String currency1, String currency2, Integer cost2up, Integer cost1Down) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO logs (`nickname1`, `nickname2`, `action`, `value`, `currency1`, `currency2`, `cost2UP`, `cost1Down`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, nickname1);
            statement.setString(2, nickname2);
            statement.setString(3, action);
            statement.setInt(4, value);
            statement.setString(5, currency1);
            statement.setString(6, currency2);
            statement.setInt(7, cost2up);
            statement.setInt(8, cost1Down);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public HashMap<String, Object> getMachine(String currency) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM machines WHERE currency = '"+ currency +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                HashMap<String, Object> machine = new HashMap<>();
                machine.put("currency", resultSet.getString("currency"));
                machine.put("machine1", resultSet.getBoolean("machine1"));
                machine.put("time1", resultSet.getInt("time1"));
                machine.put("money1", resultSet.getInt("money1"));
                return machine;
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Integer getCost(String currency) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT cost FROM currencies WHERE currencyName = '"+ currency +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean replaceLeader(String currency, String ownerName, String ownerNick) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE currencies SET "+ ownerName +"="+ ownerNick +" WHERE currencyName = '" + currency +"'");
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String getOwner1(String currency) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT owner1 FROM currencies WHERE currencyName = '"+ currency +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void changeCost(String currency, Integer newValue) {
        try {
            if (newValue <= 1) {
                newValue = 2;
            }

            PreparedStatement statement = connection.prepareStatement("UPDATE currencies SET cost="+ newValue +" WHERE currencyName = '" + currency +"'");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void buyMachine(String currencyName) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE machines SET purchased1=1 WHERE currency = '" + currencyName +"'");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void upLvlTime(String currencyName) {
        try {
            Integer lvl = getLvlTime(currencyName);
            PreparedStatement statement = connection.prepareStatement("UPDATE machines SET timeLvl=" + (lvl + 1) + " WHERE currency='" + currencyName +"'");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upLvlMoney(String currencyName) {
        try {
            Integer lvl = getLvlMoney(currencyName);
            PreparedStatement statement = connection.prepareStatement("UPDATE machines SET moneyLvl=" + (lvl + 1) + " WHERE currency='" + currencyName +"'");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Integer getLvlTime(String currencyName) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT timeLvl FROM machines WHERE currency = '"+ currencyName +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer getLvlMoney(String currencyName) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT moneyLvl FROM machines WHERE currency = '"+ currencyName +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void removeMoneyFromPlayer(String player, String currencyName, Integer value) {
        try {
            Integer money = getPlayerMoney(currencyName, player);
            PreparedStatement statement = connection.prepareStatement("UPDATE " + currencyName + " SET money=" + (money - value) + " WHERE nickname='" + player + "'");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean purchased(String currencyName, String purchasedNum) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT "+ purchasedNum +" FROM machines WHERE currency = '"+ currencyName +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void minusMoney(String currencyName, String moneyNum, Integer value) {
        try {
            Integer moneys = getMoney(currencyName, moneyNum);
            PreparedStatement statement;
            if (value < moneys) {
                statement = connection.prepareStatement("UPDATE machines SET "+ moneyNum +"="+ (moneys - value) +" WHERE currency='" + currencyName + "'");
            } else {
                statement = connection.prepareStatement("UPDATE machines SET "+ moneyNum +"=0 WHERE currency='" + currencyName + "'");
            }
            statement.executeUpdate();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public String minusTime(String currencyName, String timeNum) {
        try {
            Integer minutes = getTime(currencyName, timeNum);
            PreparedStatement statement;
            if (1 < minutes) {
                Integer lvl = getLvlTime(currencyName);
                Integer minMinute = plugin.getConfig().getInt("settings.machines.update.time.lvl" + lvl + ".minMinute");
                if (minMinute > (minutes - 1)) {
                    return ChatColor.RED + "Для вашего уровня стоит ограничение в " + minMinute + " минут(у/ы)";
                }
                statement = connection.prepareStatement("UPDATE machines SET "+ timeNum +"="+ (minutes - 1) +" WHERE currency='" + currencyName + "'");
            } else {
                statement = connection.prepareStatement("UPDATE machines SET "+ timeNum +"=1 WHERE currency='" + currencyName + "'");
            }
            statement.executeUpdate();
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String plusTime(String currencyName, String timeNum) {
        try {
            Integer moneys = getMoney(currencyName, timeNum);
            PreparedStatement statement;
            if ((moneys + 1) > 60 || (moneys + 1) < 0) {
                return ChatColor.RED + "Станок ограничен часовой переодичностью";
            }
            statement = connection.prepareStatement("UPDATE machines SET "+ timeNum +"="+ (moneys + 1) +" WHERE currency='" + currencyName + "'");
            statement.executeUpdate();
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String plusMoney(String currencyName, String moneyNum, Integer value) {
        try {
            Integer moneys = getMoney(currencyName, moneyNum);
            PreparedStatement statement;
            if ((moneys + value) > 2000000000 || (moneys + value) < 0) {
                return ChatColor.RED + "Ограничение в 2 млрд превышать не советую";
            }
            Integer lvl = getLvlMoney(currencyName);
            Integer maxMoney = plugin.getConfig().getInt("settings.machines.update.money.lvl" + lvl + ".maxMoney");
            if ((moneys + value) > maxMoney) {
                return ChatColor.RED + "Ограничение вашего уровня - " + maxMoney;
            }
            statement = connection.prepareStatement("UPDATE machines SET "+ moneyNum +"="+ (moneys + value) +" WHERE currency='" + currencyName + "'");
            statement.executeUpdate();
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }


    public ArrayList<HashMap<String, Object>> getLogs(String currency, Integer limit) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM logs WHERE currency1 = '"+ currency +"' OR currency2 = '"+ currency +"' ORDER BY id DESC LIMIT " + limit + ";");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<HashMap<String, Object>> res = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Object> resmap = new HashMap<>();
                resmap.put("id", resultSet.getInt("id"));
                resmap.put("nickname1", resultSet.getString("nickname1"));
                resmap.put("nickname2", resultSet.getString("nickname2"));
                resmap.put("action", resultSet.getString("action"));
                resmap.put("value", resultSet.getInt("value"));
                resmap.put("currency1", resultSet.getString("currency1"));
                resmap.put("currency2", resultSet.getString("currency2"));
                resmap.put("cost2UP", resultSet.getInt("cost2UP"));
                resmap.put("cost1Down", resultSet.getInt("cost1Down"));

                res.add(resmap);
            }
            return res;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    public Integer getMoney(String currencyName, String moneyNum) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT "+ moneyNum +" FROM machines WHERE currency = '"+ currencyName +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getOwners(String currency) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT `owner1`, `owner2`, `owner3` FROM `currencies` WHERE currencyName = '"+ currency +"';");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<String> owners = new ArrayList<>();
            while (resultSet.next()) {
                owners.add(resultSet.getString("owner1"));
                owners.add(resultSet.getString("owner2"));
                owners.add(resultSet.getString("owner3"));
            }
            return owners;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<String> getCurrencies() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT `currencyName` FROM `currencies`;");
            ResultSet resultSet = statement.executeQuery();
            ArrayList<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            return tables;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<Integer, String> getCurrenciesId() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT `currencyName`, `id` FROM `currencies`;");
            ResultSet resultSet = statement.executeQuery();
            HashMap<Integer, String> tables = new HashMap<>();
            while (resultSet.next()) {
                tables.put(resultSet.getInt(2), resultSet.getString(1));
            }
            return tables;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getValuteById(Integer id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT currencyName FROM currencies WHERE id="+ id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean switchMachine(String currencyName, String machine){
        try {
            boolean machineEnable = enableMachine(currencyName, machine);
            PreparedStatement statement;
            if (machineEnable) {
                statement = connection.prepareStatement("UPDATE machines SET "+ machine +"=0 WHERE currency='" + currencyName + "'");
            } else {
                statement = connection.prepareStatement("UPDATE machines SET "+ machine +"=1 WHERE currency='" + currencyName + "'");
            }
            statement.executeUpdate();
            return !machineEnable;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String getCurrencyForPlayer(String player) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT currencyName FROM currencies WHERE owner1 = '" + player + "' OR owner2 = '" + player + "' OR owner3 = '" + player + "'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public ResultSet getPlayersForCurrency(String currency) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT owner1, owner2, owner3 FROM currencies WHERE currencyName='"+ currency +"'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet;
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean enableMachine(String currencyName, String machine) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT " + machine + " FROM machines WHERE currency = '" + currencyName + "'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean giveMoney(String currencyName, String player, Integer money) {
        try {
            Integer moneyPlayer = getPlayerMoney(currencyName, player);
            boolean pExist = playerExists(player, currencyName);
            PreparedStatement statement;
            if (pExist) {
                statement = connection.prepareStatement("UPDATE " + currencyName + " SET money=" + (moneyPlayer + money) + " WHERE nickname='" + player + "'");
            } else {
                statement = connection.prepareStatement("INSERT INTO " + currencyName + " (`nickname`, `money`) VALUES (?, ?)");
                statement.setString(1, player);
                statement.setInt(2, (moneyPlayer + money));
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Integer getTime(String currencyName, String timeName) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT " + timeName + " FROM machines WHERE currency = '" + currencyName + "'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean transfer (String firstPlayer, String secondPlayer, Integer value, String currencyName) {
        try {

            Integer moneyPlayer = getPlayerMoney(currencyName, firstPlayer);
            if (value == 0) {
                return false;
            }

            if (firstPlayer.equalsIgnoreCase(secondPlayer)) {
                return false;
            }

            if (moneyPlayer >= value) {
                if (playerExists(secondPlayer, currencyName)) {
                    Integer secondMoneyPlayer = getPlayerMoney(currencyName, secondPlayer);
                    PreparedStatement statement = connection.prepareStatement("UPDATE " + currencyName + " SET money=" + (secondMoneyPlayer + value) + " WHERE nickname='" + secondPlayer + "'");
                    PreparedStatement statement2 = connection.prepareStatement("UPDATE " + currencyName + " SET money=" + (moneyPlayer - value) + " WHERE nickname='" + firstPlayer + "'");
                    statement.executeUpdate();
                    statement2.executeUpdate();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }


        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean playerExists(String nick, String currencyName) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + currencyName + "` WHERE nickname = '" + nick + "'" );
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            } else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    public Integer getPlayerMoney(String currencyName, String nickname) {
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT money FROM " + currencyName + " WHERE nickname = '" + nickname + "'");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public boolean createNewCurrency(String currencyName, String creator, String owner1, String owner2, String owner3, Integer cost, Integer cash) {
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + currencyName + "(id integer primary key auto_increment, `nickname` varchar(100), `money` integer) CHARACTER SET utf8 COLLATE utf8_general_ci;");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO currencies (`currencyName`, `creator`, `owner1`, `owner2`, `owner3`, `cost`) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, currencyName);
            statement.setString(2, creator);
            statement.setString(3, owner1);
            statement.setString(4, owner2);
            statement.setString(5, owner3);
            statement.setInt(6, cost);
            statement.executeUpdate();


            statement = connection.prepareStatement("INSERT INTO machines (`currency`) VALUES (?)");

            statement.setString(1, currencyName);
            statement.executeUpdate();

            boolean cash1 = giveMoney(currencyName, owner1, cash);
            boolean cash2 = giveMoney(currencyName, owner2, cash);
            boolean cash3 = giveMoney(currencyName, owner3, cash);

            if (cash1 & cash2 & cash3) {
                return true;
            } else {
                return false;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
