package com.example.r4_fry.androidnodeapp.NodeDiagram;

import android.support.annotation.Nullable;

import com.example.r4_fry.androidnodeapp.CompaniesDB.Company;
import com.example.r4_fry.androidnodeapp.CompaniesDB.Officer;
import java.util.ArrayList;

/**
 * Class to hold data on a specified node on the diagram
 */
public class Node {
    // data nodes
    public final Company companyData;
    public final Officer officerData;

    // coordinate nodes
    public int xCoord;
    public int yCoord;

    // child nodes TODO: implement sub node trees
    public ArrayList<Node> subNodeArrayList;


    /**Constructor for creating new node to display in the diagram
     * @param x horizontal coordinate on screen of centre of node
     * @param y vertical coordinate on screen of centre of node
     * @param companyData data for a company
     * @param officerData data for an officer
     */
    public Node(int x, int y, @Nullable Company companyData, @Nullable Officer officerData) {
        this.companyData = companyData;
        this.officerData = officerData;
        this.xCoord = x;
        this.yCoord = y;
        subNodeArrayList = new ArrayList<>();
    }

}
