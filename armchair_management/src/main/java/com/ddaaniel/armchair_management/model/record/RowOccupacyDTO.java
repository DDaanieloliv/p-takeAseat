package com.ddaaniel.armchair_management.model.record;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RowOccupacyDTO {
  public Integer fileira;
  public Integer total_assentos;
  public Integer assentos_livre;
  public BigDecimal taxa_ocupacao_percentual;
}
