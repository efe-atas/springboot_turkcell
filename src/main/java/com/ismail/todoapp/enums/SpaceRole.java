package com.ismail.todoapp.enums;

public enum SpaceRole {
    OWNER,  // Space'i siler, üye atar, her şeyi yapar
    ADMIN,  // Task siler, düzenler, üye ekler
    EDITOR, // Task ekler, kendi taskını düzenler, task tamamlar
    VIEWER  // Sadece izler
}