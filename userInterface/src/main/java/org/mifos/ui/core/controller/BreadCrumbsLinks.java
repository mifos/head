package org.mifos.ui.core.controller;

import java.util.LinkedList;
import java.util.List;

public class BreadCrumbsLinks {
private static List<String> wayList;

    static {
        wayList = new LinkedList<String>();
    }

    public List<String> getWay(){
        return wayList;
    }

    public void setWay(String pa){
        int j=0;
        //checking for any duplicates in the list as there will be a duplicate if the user returns to the page he visited previously
        j=wayList.indexOf(pa);
        if(j>=0){
            //removing all those entries after the entry which is found to be duplicate.
            wayList=wayList.subList(0,j+1);
        }else{
            //If there is no duplicate then the link is added to the list.
        wayList.add(wayList.size(),pa);
        }
    }
}
