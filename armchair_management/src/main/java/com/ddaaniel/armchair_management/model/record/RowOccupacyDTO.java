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
  public Integer totalAssentos;
  public Integer assentosLivre;
  public BigDecimal taxaOcupacaoPercentual;
}
