/*
 * Created by Dominik Szaradowski on 27.05.19 15:42
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

public class Currency {
    private String name;
    private String prefix;
    private String suffix;

    public Currency(String name, String prefix, String suffix){
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
