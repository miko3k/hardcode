package org.deletethis.hardcode.examples;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.deletethis.hardcode.Hardcode;

import java.util.ArrayList;

class Items {
    ArrayList<Item> items;

    public Items(ArrayList<Item> items) {
        this.items = items;
    }
}

class Item {
    private String value;

    public Item(String value) {
        this.value = value;
    }
}

public class References {
    public static void main(String[] args) {
        Item item = new Item("foo");
        ArrayList<Item> data = new ArrayList<>();
        data.add(item);
        data.add(item);

        Hardcode hardcode = Hardcode.defaultConfig();
        TypeSpec typeSpec = hardcode.createClass("ReferencesSupplier", new Items(data));
        System.out.println(JavaFile.builder(References.class.getPackage().getName(), typeSpec).build());
    }
}
