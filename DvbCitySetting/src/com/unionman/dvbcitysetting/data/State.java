package com.unionman.dvbcitysetting.data;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/10/31.
 */
public class State extends CityBase{
    private ArrayList<City> cities = new ArrayList<City>();

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public void addCity(City city) {
        cities.add(city);
    }
}
