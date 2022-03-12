package com.example.todolist;

import android.widget.ImageView;

import java.util.ArrayList;

public class Tag {

    private static ArrayList<Tag> tagList = new ArrayList<Tag>();
    public String name, icon;

    public Tag(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public static void initUsers()
    {
        Tag home = new Tag("home", "home");
        tagList.add(home);

        Tag work = new Tag("work", "work");
        tagList.add(work);

        Tag health = new Tag("health", "health");
        tagList.add(health);

        Tag study = new Tag("study", "study");
        tagList.add(study);

        Tag shopping = new Tag("shopping", "shopping");
        tagList.add(shopping);

    }

    public int getImage()
    {
        switch (getName())
        {
            case "home":
                return R.drawable.home;
            case "work":
                return R.drawable.work;
            case "health":
                return R.drawable.health;
            case "study":
                return R.drawable.study;
            case "shopping":
                return R.drawable.shopping;

        }
        return R.drawable.none;
    }

    public static ArrayList<Tag> getUserArrayList()
    {
        return tagList;
    }
}
