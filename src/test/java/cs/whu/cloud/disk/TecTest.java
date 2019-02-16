package cs.whu.cloud.disk;

import org.junit.Test;

import java.util.Enumeration;
import java.util.Properties;

public class TecTest {

    @Test
    public void getOsName(){
        Properties properties=System.getProperties();
        Enumeration enums=properties.propertyNames();
        while(enums.hasMoreElements()){
            Object obj=enums.nextElement();
            System.out.printf("\"%-12s\"   :   \"%s\"\n",obj,properties.get(obj));
        }
    }
}
