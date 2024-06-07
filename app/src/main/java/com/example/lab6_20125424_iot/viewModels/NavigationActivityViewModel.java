package com.example.lab6_20125424_iot.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lab6_20125424_iot.item.ListElementEgreso;
import com.example.lab6_20125424_iot.item.ListElementIngreso;

import java.util.ArrayList;

public class NavigationActivityViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ListElementEgreso>> listaEgreso = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ListElementIngreso>> listaIngreso = new MutableLiveData<>();

    public MutableLiveData<ArrayList<ListElementEgreso>> getListaEgreso() {
        return listaEgreso;
    }

    public void setListaEgreso(MutableLiveData<ArrayList<ListElementEgreso>> listaEgreso) {
        this.listaEgreso = listaEgreso;
    }

    public MutableLiveData<ArrayList<ListElementIngreso>> getListaIngreso() {
        return listaIngreso;
    }

    public void setListaIngreso(MutableLiveData<ArrayList<ListElementIngreso>> listaIngreso) {
        this.listaIngreso = listaIngreso;
    }
}
