package com.chen1144.agent;

import java.util.Arrays;

public class ByteList {
    private byte[] bytes;
    private int size;

    public ByteList(){
        this(10);
    }

    public ByteList(int capacity){
        this.bytes = new byte[capacity];
        this.size = 0;
    }

    public ByteList(byte[] bytes){
        this.bytes = bytes;
        this.size = bytes.length;
    }

    public void put(byte b){
        if(size >= bytes.length){
            bytes = Arrays.copyOf(bytes, size * 2);
        }
        bytes[size] = b;
        size++;
    }

    public void putAll(byte[] bytes){
        int newSize = size + bytes.length;
        if(newSize > bytes.length){
            this.bytes = Arrays.copyOf(this.bytes, newSize);
        }
        System.arraycopy(bytes, 0, this.bytes, size, bytes.length);
        size = newSize;
    }

    public int size(){
        return size;
    }

    public byte[] takeBytes(){
        if(size == bytes.length){
            byte[] result = bytes;
            bytes = new byte[size];
            size = 0;
            return result;
        }else{
            byte[] result =  Arrays.copyOf(bytes, size);
            size = 0;
            return result;
        }
    }

    public void clear(){
        size = 0;
    }

    public void pop(){
        size--;
    }
}
