package com.unionman.dvbcitysetting.data;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/10/31.
 */
public class Province extends CityBase{
    private ArrayList<State> states = new ArrayList<State>();

    public ArrayList<State> getStates() {
        return states;
    }

    public void setStates(ArrayList<State> states) {
        this.states = states;
    }

    public void addState(State state) {
        states.add(state);
    }
}
