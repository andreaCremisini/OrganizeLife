package com.example.andrea.organizelife;

import android.content.Context;

/**
 * Created by andrea on 23/04/2016.
 */

public class Category {

  public String id;
  public String name;
  public String imageName;
  public boolean isFav;

  public int getImageResourceId(Context context) {
    return context.getResources().getIdentifier(this.imageName, "drawable", context.getPackageName());
  }
}
