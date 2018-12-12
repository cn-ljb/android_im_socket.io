package com.codebear.keyboard.data;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/7/1.
 */

public class AppFuncBean {
    private int id;
    private Object icon;
    private String title;

    public AppFuncBean() {
    }

    public AppFuncBean(int id, Object icon, String title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
