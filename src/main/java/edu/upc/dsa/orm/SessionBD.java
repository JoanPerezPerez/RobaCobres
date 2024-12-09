package edu.upc.dsa.orm;

import edu.upc.dsa.models.useritemcharacterrelation;
import edu.upc.dsa.orm.util.ObjectHelper;
import edu.upc.dsa.orm.util.QueryHelper;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

public interface SessionBD {
    public void save(Object entity); //FUNCIONA
    //Simplement amb possar l'objecte ja ho guarda a la basse corresponent,
    public void close();
    //Tanca la connexió
    public Object get(Class theClass, Object key, Object value); //FUNCIONA
    //Per utilitzar-ho, cal indicar quina és el tipus que volem (theClass)
    //Quina clau utilitzem per buscar-ho (nom, correu, url...)
    //I quin valor te la clau (pepito, pepito@gmail.com...)
    public void update(Object object, Object Key, Object value);
    //Per utilitzar-ho cal donar l'objecte en el que volem fer l'update (sencer)
    //la clau i el valor de la clau
    public void delete(Class theClass, Object Key, Object value); //FUNCIONA  // cruD
    //Per utilitzar-ho, cal indicar quina és el tipus que volem (theClass)
    //Quina clau utilitzem per buscar-ho (nom, correu, url...)
    //I quin valor te la clau (pepito, pepito@gmail.com...)
    public void deleteAll(Class TheClass);
    //Per utilitzar-ho, cal indicar quina és el tipus que volem (theClass)
    public <T> List<T> findAll(Class<T> theClass); //Funciona
    //Per utilitzar-ho, cal indicar quina és el tipus que volem (theClass)
    public <T> List<T> findAllWithConditions(Class<T> theClass, HashMap params); //Funciona
    //Per utilitzar-ho, cal indicar quina és el tipus que volem (theClass)
    //El HashMap conté Clau + valor (exemple--> nom,Pepito)
    //ATENCIÓ LA CLAU HA DE CONTENIR EL SÍMBOL QUE VOLEM, = < >...
    public <T> List<T> query(String query, Class theClass, HashMap params);
    public void buy(Object userKey, Object userValue, Object itemKey, Object itemValue,Object characterKey, Object characterValue);
    // Per utilitzar-ho cal dir la clau + valor de tot, en l'ordre que toca:
    //1--> Clau + valor de l'usuari (nom,pepito)
    //2--> Clau + valor de item (nom,sierra)
    //3--> Clau + valor de character (nom,primer)
    public <T> List<T> getRelaciones(Class<T> theClass, Object key, Object value);
    //Per utilitzar-ho, cal indicar quina és el tipus que volem (theClass)
    //Quina clau utilitzem per buscar-ho (nom, correu, url...)
    //I quin valor te la clau (pepito, pepito@gmail.com...)
    public <T> List<T> findObjectNotBoughtForUser(Class<T> theClass, Object key, Object value);

}
