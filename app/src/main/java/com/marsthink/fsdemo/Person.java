/*
 *     Copyright (c) 2016 Meituan Inc.
 *
 *     The right to copy, distribute, modify, or otherwise make use
 *     of this software may be licensed only pursuant to the terms
 *     of an applicable Meituan license agreement.
 *
 */

package com.marsthink.fsdemo;

import java.io.Serializable;

class Person implements Serializable //writeObject()会抛出NotSerializableException异常，所以继承接口Serializable
{
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String tostring() {
        return name + ":" + age;
    }
}  
