package com.example.andrea.organizelife;

import java.util.ArrayList;

/**
 * Created by andrea on 23/04/2016.
 */

public class CategoryData {

    public static String[] placeNameArray = {"University", "Work", "Home", "Shopping", "Travel", "Restaurants", "Sports", "Project", "Grocery", "TV"};

    public static ArrayList<Category> placeList() {
        ArrayList<Category> list = new ArrayList<>();
        for (int i = 0; i < placeNameArray.length; i++) {
            Category category = new Category();
            category.name = placeNameArray[i];
            category.imageName = placeNameArray[i].replaceAll("\\s+", "").toLowerCase();
            if (i == 2 || i == 5) {
                category.isFav = true;
            }
            list.add(category);
        }
        return (list);
    }
}
