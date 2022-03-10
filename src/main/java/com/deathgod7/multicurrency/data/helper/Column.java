package com.deathgod7.multicurrency.data.helper;

import com.deathgod7.multicurrency.data.sqlite.SQLite;

public class Column {

    public String name;
    public SQLite.DataType dataType;
    public int limit = 0;
    private Object value;

    public Column(String name, SQLite.DataType dataType) {
        this.name = name;
        this.dataType = dataType;
        this.limit = 0;
        this.value = 0;
    }

    public Column(String name, SQLite.DataType dataType, int limit) {
        this.name = name;
        this.dataType = dataType;
        this.limit = limit;
        this.value = 0;
    }

    public Column(String name, Object value, SQLite.DataType dataType) {
        this.name = name;
        this.dataType = dataType;
        this.limit = 0;
        this.value = value;
    }

    public Column(String name, Object value, SQLite.DataType dataType, int limit) {
        this.name = name;
        this.dataType = dataType;
        this.limit = limit;
        this.value = value;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SQLite.DataType getDataType() {
        return dataType;
    }

    public void setDataType(SQLite.DataType dataType) {
        this.dataType = dataType;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
