package edu.upc.dsa.orm;
import edu.upc.dsa.ItemManagerImpl;
import edu.upc.dsa.models.Item;
import edu.upc.dsa.models.GameCharacter;
import edu.upc.dsa.models.User;
import edu.upc.dsa.models.useritemcharacterrelation;
import edu.upc.dsa.orm.util.ObjectHelper;
import edu.upc.dsa.orm.util.QueryHelper;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SessionImpl implements SessionBD {
    private final Connection conn;
    final static Logger logger = Logger.getLogger(SessionImpl.class);


    public SessionImpl(Connection conn) {
        this.conn = conn;
    }

    public void save(Object entity) {
        //Hi ha taules que tenen un ID, i altres que no, per això cal fer aquesta "distinció"
        if (entity.getClass() == useritemcharacterrelation.class) {
            try {
                String insertQuery = QueryHelper.createQueryINSERT(entity);
                // INSERT INTO User (ID, lastName, firstName, address, city) VALUES (0, ?, ?, ?,?)
                PreparedStatement pstm = null;
                pstm = conn.prepareStatement(insertQuery);
                int i = 1;
                for (String field : ObjectHelper.getFields(entity)) {
//                    Object x = ObjectHelper.getter(entity, field);
                    if (ObjectHelper.getter(entity, field).toString().equals("0")) {
                        pstm.setObject(i++, null);
                    } else {
                        pstm.setObject(i++, ObjectHelper.getter(entity, field));
                    }
                }
                pstm.executeQuery();
            } catch (SQLException e) {
                logger.warn("Error in the save to DB function: "+ e);
            }
        } else {
            try {
                String insertQuery = QueryHelper.createQueryINSERTWithID(entity);
                // INSERT INTO User (ID, lastName, firstName, address, city) VALUES (0, ?, ?, ?,?)
                PreparedStatement pstm = null;
                pstm = conn.prepareStatement(insertQuery);
                pstm.setObject(1, 0);
                int i = 2;
                for (String field : ObjectHelper.getFields(entity)) {
                    pstm.setObject(i++, ObjectHelper.getter(entity, field));
                }
                pstm.executeQuery();
            } catch (SQLException e) {
                logger.warn("Error in the save to DB function: "+ e);
            }
        }
    }

    public void close() {
        try{
            this.conn.close();
        }
        catch (SQLException ex){
            logger.warn("Attention, connection to the DDBB closed");
        }

    }
    public  Object get(Class theClass, Object keyQ, Object valueQ){
        try {
            String sql = QueryHelper.createQuerySELECT(theClass,keyQ);
            //Crea un nou object del tipus theClass
            Object o = theClass.newInstance();

            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1,valueQ);
            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                ResultSetMetaData rsmd = res.getMetaData();

                int numColumns = rsmd.getColumnCount();
                int i = 1;

                while (i < numColumns + 1) {
                    String key = rsmd.getColumnName(i);
                    Object value = res.getObject(i);

                    ObjectHelper.setter(o, key, value);
                    i++;
                }
                return o;
            }
            logger.info("No "+theClass.getSimpleName()+" found");
            return null;
        } catch (Exception ex) {
            logger.warn("Error in the get function: "+ ex);
            return null;
        }
    }


    public  void update(Object o, Object Key, Object value){
        String sql = QueryHelper.createQueryUPDATE(o,Key);
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql);
            int i = 1;
            for (String field : ObjectHelper.getFields(o)) {
                pstm.setObject(i++, ObjectHelper.getter(o, field));
            }
            pstm.setObject(i, value);
            pstm.executeQuery();
        }
        catch (SQLException e) {
            logger.warn("Error in the update function: "+ e);
        }
    }


    public void delete(Class theClass, Object Key, Object value) {
        try{
//          String sql = "DELETE FROM " + tableName + " WHERE ID = ?";
            String sql = QueryHelper.createQueryDELETE(theClass, Key);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1, value);
            ResultSet res = pstm.executeQuery();
        }
        catch(Exception ex){
            logger.warn("Error in the delete function: "+ ex);
        }
    }

    public  void deleteAll(Class TheClass){
        try{
//          String sql = "DELETE FROM " + tableName;
            String sql = QueryHelper.createQueryDELETEALL(TheClass);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            ResultSet res = pstm.executeQuery();
        }
        catch(Exception ex){
            logger.warn("Error in the deleteAll function: "+ ex);
        }
    }

    public <T> List<T> findAll(Class<T> theClass) {
        try{
            String sql = QueryHelper.createQuerySELECTALL(theClass);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            ResultSet res = pstm.executeQuery();
            ArrayList<String> fields = new ArrayList<>(Arrays.asList(ObjectHelper.getFieldsDirectlyClass(theClass)));
            List<T> lists = new ArrayList<>();
            while (res.next()) {
                T o = theClass.newInstance();
                for(String field: fields) ObjectHelper.setter(o, field, res.getObject(field));
                lists.add(o);
            }
            return lists;
        }
        catch(Exception ex){
            logger.warn("Error in the findAll function: "+ ex);
            return null;
        }
    }

    public <T> List<T> findObjectNotBoughtForUser(Class<T> theClass,Object key,Object value) {
        String IDUser = "";
        try {
            String sql = QueryHelper.createQuerySELECT(User.class, key);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1, value);
            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();
                int i = 1;
                while (i < numColumns + 1 && IDUser.isEmpty()) {
                    String keyRes = rsmd.getColumnName(i);
                    Object valueRes = res.getObject(i);
                    if (keyRes.equals("ID")) IDUser = String.valueOf(valueRes);
                    i++;
                }
            }
        }
        catch (Exception e) {
            logger.warn("Error in the findObjectNotBoughtForUser function when finding the User ID: "+ e);
        }

        try{
            String theQuery = QueryHelper.createSelectIDWhereNotIn(theClass);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(theQuery);

            pstm.setObject(1,IDUser);
            ResultSet res = pstm.executeQuery();

            ArrayList<String> fields = new ArrayList<>(Arrays.asList(ObjectHelper.getFieldsDirectlyClass(theClass)));
            List<T> lists = new ArrayList<>();
            while (res.next()) {
                T o = theClass.newInstance();
                for(String field: fields) ObjectHelper.setter(o, field, res.getObject(field));
                lists.add(o);
            }
            return lists;
        }
        catch(Exception ex){
            logger.warn("Error in the findObjectNotBoughtForUser: "+ ex);
            return null;
        }
    }

    public <T> List<T> findAllWithConditions(Class<T> theClass, HashMap params) {
        try{
            String theQuery = QueryHelper.createSelectFindAll(theClass, params);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(theQuery);

            int i=1;
            for (Object value : params.values()) {
                pstm.setObject(i++, value );
            }
            ResultSet res = pstm.executeQuery();

            ArrayList<String> fields = new ArrayList<>(Arrays.asList(ObjectHelper.getFieldsDirectlyClass(theClass)));
            List<T> lists = new ArrayList<>();
            while (res.next()) {
                T o = theClass.newInstance();
                for(String field: fields) ObjectHelper.setter(o, field, res.getObject(field));
                lists.add(o);
            }
            return lists;
        }
        catch(Exception ex){
            logger.warn("Error in the findAll with conditions function: "+ ex);
            return null;
        }
    }

    public List<Object> query(String query, Class theClass, HashMap params) {
        return null;
    }

    public void buy(Object userKey, Object userValue, Object itemKey, Object itemValue,Object characterKey, Object characterValue){
        useritemcharacterrelation rel = new useritemcharacterrelation();
        try {
            String sql = QueryHelper.createQuerySELECT(User.class,userKey);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1,userValue);
            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();
                int i = 1;
                while (i < numColumns + 1 && rel.getID_User()==0) {
                    String key = rsmd.getColumnName(i);
                    Object value = res.getObject(i);
                    if(key.equals("ID")) rel.setID_User((Integer) value);
                    i++;
                }

            }
        } catch (Exception ex) {
            logger.warn("Error in the buy function when finding the User ID: "+ ex);
        }
        try {
            String sql = QueryHelper.createQuerySELECT(Item.class,itemKey);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1,itemValue);
            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();
                int i = 1;
                while (i < numColumns + 1 && rel.getID_Item()==0) {
                    String key = rsmd.getColumnName(i);
                    Object value = res.getObject(i);
                    if(key.equals("ID")) rel.setID_Item((Integer) value);
                    i++;
                }
            }
        } catch (Exception ex) {
            logger.warn("Error in the buy function when finding the Item ID: "+ ex);
        }
        try {
            String sql = QueryHelper.createQuerySELECT(GameCharacter.class,characterKey);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1,characterValue);
            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();
                int i = 1;
                while (i < numColumns + 1 && rel.getID_GameCharacter()==0) {
                    String key = rsmd.getColumnName(i);
                    Object value = res.getObject(i);
                    if(key.equals("ID"))  rel.setID_GameCharacter((Integer) value);
                    i++;
                }
            }
        } catch (Exception ex) {
            logger.warn("Error in the buy function when finding the Character ID: "+ ex);
        }
        this.save(rel);
    }

    public <T> List<T> getRelaciones(Class<T> theClass, Object key, Object value){
        String IDUser = "";
        try {
            String sql = QueryHelper.createQuerySELECT(User.class, key);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1, value);
            ResultSet res = pstm.executeQuery();
            if (res.next()) {
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();
                int i = 1;
                while (i < numColumns + 1 && IDUser.isEmpty()) {
                    String keyRes = rsmd.getColumnName(i);
                    Object valueRes = res.getObject(i);
                    if (keyRes.equals("ID")) IDUser = String.valueOf(valueRes);
                    i++;
                }
            }
        }
        catch (Exception e) {
            logger.warn("Error in the getRelation function when finding the User ID: "+ e);
        }
        try{
            String sql = QueryHelper.createQueryGetWithRelations(theClass);
            PreparedStatement pstm = null;
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1,IDUser);
            ResultSet res = pstm.executeQuery();
            List<T> lists = new ArrayList<>();
            while (res.next()) {
                T o = theClass.newInstance();
                ResultSetMetaData rsmd = res.getMetaData();
                int numColumns = rsmd.getColumnCount();
                int i = 5;
                while(i<numColumns+1){
                    String keyRes = rsmd.getColumnName(i);
                    Object valueRes = res.getObject(i);
                    ObjectHelper.setter(o, keyRes, valueRes);
                    i++;
                }
                lists.add(o);
            }
            return lists;
        } catch (Exception ex) {
            logger.warn("Error in the getRelation function when finding the "+theClass.getSimpleName()+ ": "+ ex);
            return null;
        }
    }
}
