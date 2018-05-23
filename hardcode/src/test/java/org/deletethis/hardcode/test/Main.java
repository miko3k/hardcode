package org.deletethis.hardcode.test;

import org.deletethis.hardcode.Hardcoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static void doIt(Hardcoder hardcoder, Object o) {
        System.out.println(hardcoder.method("foo", o));
    }

    
    enum Enm {
        BAR,
        BAZ
    }
    
    public static void main(String[] args) {
        Hardcoder bld = Hardcoder.builtinConfig();

        doIt(bld, "hello world\n");
        doIt(bld, new Data("hello", 1, 50L));

            
        Data data = new Data(null, 1, 50L);
        ArrayList<Data> arr = new ArrayList<>(2);
        arr.add(data);
        arr.add(data);
        doIt(bld, arr);
        Enm enm = Enm.BAR;
        
        doIt(bld, enm);
        doIt(bld, new ExtData(true, "foo", 1, Long.MIN_VALUE));
        
        Map<String, Data> map = new HashMap<>();
        map.put("data1", data);
        map.put("data2", data);
        doIt(bld, map);

        /*
        Container<Container> a = new Container<>();
        Container<Container> b = new Container<>();
        a.setValue(b);
        b.setValue(a);
        System.out.println(new Hardcoder().value(a));
        */

    }
}
