package com.surffountain.browser.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TabSessionEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String url;
    public String title;
}
