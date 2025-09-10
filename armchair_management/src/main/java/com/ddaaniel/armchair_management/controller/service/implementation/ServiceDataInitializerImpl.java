package com.ddaaniel.armchair_management.controller.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddaaniel.armchair_management.config.Config;
import com.ddaaniel.armchair_management.controller.service.IDataInitializerService;
import com.ddaaniel.armchair_management.model.repository.ISeatRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class ServiceDataInitializerImpl implements IDataInitializerService {

  private static final Logger logger = LoggerFactory.getLogger(ServiceDataInitializerImpl.class);

  private final ISeatRepository seatRepository;
  private final Config appConfig;

  @Autowired
  public ServiceDataInitializerImpl (ISeatRepository seatRepository, Config appConfig) {
    this.seatRepository = seatRepository;
    this.appConfig = appConfig;
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
  }
}
