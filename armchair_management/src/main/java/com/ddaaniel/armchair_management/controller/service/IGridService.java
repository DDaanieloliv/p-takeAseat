package com.ddaaniel.armchair_management.controller.service;

import java.util.ArrayList;

import com.ddaaniel.armchair_management.model.Seat;

public interface IGridService {

  ArrayList<ArrayList<Seat>> currentGrid();

}
