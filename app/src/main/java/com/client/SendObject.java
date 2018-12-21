package com.client;

import java.util.ArrayList;
import java.util.List;

public class SendObject implements java.io.Serializable {
    public byte[] imgByte;
    public String divnum;
    public ArrayList<String> listenCard=new ArrayList<>();
    public SendObject(byte data[] , String divNum)
    {
        this.imgByte=data;
        this.divnum=divNum;

    }

    public void setListenCard(ArrayList<String> listenCard) {
        this.listenCard = listenCard;
    }
}
