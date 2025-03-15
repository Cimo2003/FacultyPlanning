package com.oussama.FacultyPlanning.Enum;

public enum Day {
    FRIDAY(0, "الجمعة"),
    SATURDAY(1, "السبت"),
    SUNDAY(2, "الأحد"),
    MONDAY(3, "الإثنين"),
    TUESDAY(4, "الثلاثاء"),
    WEDNESDAY(5, "الأربعاء"),
    THURSDAY(6, "الخميس");


    private final int value;
    private final String name;

    Day(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getDayName(int value) {
        for (Day day : values()) {
            if (day.value == value) {
                return day.name;
            }
        }
        throw new IllegalArgumentException("Invalid day value: " + value);
    }
}