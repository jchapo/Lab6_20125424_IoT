package com.example.lab6_20125424_iot.dataHolder;

import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.example.lab6_20125424_iot.item.ListElementEgreso;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static DataManager instance;

    private List<ListElementIngreso> ingresosList;
    private List<ListElementEgreso> egresosList;
    private String userId;

    public static void setInstance(DataManager instance) {
        DataManager.instance = instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private DataManager() {
        ingresosList = new ArrayList<>();
        egresosList = new ArrayList<>();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<ListElementIngreso> getIngresosList() {
        return ingresosList;
    }

    public void setIngresosList(List<ListElementIngreso> ingresosList) {
        this.ingresosList = ingresosList;
    }

    public void addIngreso(ListElementIngreso ingreso) {
        ingresosList.add(ingreso);
    }

    public void removeIngreso(String id) {
        for (int i = 0; i < ingresosList.size(); i++) {
            if (ingresosList.get(i).getId().equals(id)) {
                ingresosList.remove(i);
                break;
            }
        }
    }

    public List<ListElementEgreso> getEgresosList() {
        return egresosList;
    }

    public void setEgresosList(List<ListElementEgreso> egresosList) {
        this.egresosList = egresosList;
    }

    public void addEgreso(ListElementEgreso egreso) {
        egresosList.add(egreso);
    }

    public void removeEgreso(String id) {
        for (int i = 0; i < egresosList.size(); i++) {
            if (egresosList.get(i).getId().equals(id)) {
                egresosList.remove(i);
                break;
            }
        }
    }
}
