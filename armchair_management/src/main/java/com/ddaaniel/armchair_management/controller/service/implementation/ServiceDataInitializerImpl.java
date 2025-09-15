package com.ddaaniel.armchair_management.controller.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddaaniel.armchair_management.config.Config;
import com.ddaaniel.armchair_management.controller.service.IDataInitializerService;
import com.ddaaniel.armchair_management.model.GridEntity;
import com.ddaaniel.armchair_management.model.Seat;
import com.ddaaniel.armchair_management.model.enums.SeatType;
import com.ddaaniel.armchair_management.model.repository.IGridRepository;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class ServiceDataInitializerImpl implements IDataInitializerService {

  private static final Logger logger = LoggerFactory.getLogger(ServiceDataInitializerImpl.class);

  private final ISeatRepository seatRepository;
  private final IGridRepository gridRepository;
  private final Config appConfig;

  @Autowired
  public ServiceDataInitializerImpl (ISeatRepository seatRepository, Config appConfig, IGridRepository gridRepository) {
    this.seatRepository = seatRepository;
    this.appConfig = appConfig;
    this.gridRepository = gridRepository;
  }

  @PostConstruct
  @Transactional
  @Override
  public void initilizeSeats (){
    if (!appConfig.isEnable()) {
      return;
    }

    if (seatRepository.count() > 0) {
      logger.info("Já existem assentos no banco. Pulando inicialização.");
      return;
    }

    if (gridRepository.initialGrid().isEmpty()) {
      var grid = GridEntity.builder().build();
      logger.warn("Nenhuma entidade grid com a flag 'is_itialGrid' foi encontrada. Uma Entidade Grid com a flag 'is_itialGrid foi persistida!");

      gridRepository.save(grid);
    }


    int position = 1;
    for (int countRows = 1; countRows <= appConfig.getRows(); countRows++) {

      for (int countColumns = 1; countColumns <= appConfig.getColumns(); countColumns++) {
        var entity =  Seat.builder()
        .position(position)
        .row(countRows)
        .column(countColumns)
        .status(SeatType.AVAILABLE)
        .free(true)
        .currentGrid(gridRepository.initialGrid().get())
        .build();

        // var entity = new Seat();
        // entity.setPosition(position);
        // entity.setRow(countRows);
        // entity.setColumn(countColumns);
        // entity.setStatus(SeatType.AVAILABLE);
        // entity.setFree(true);

        seatRepository.save(entity);
        position++;
      }
    }
  }
}
