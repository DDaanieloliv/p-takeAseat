package com.ddaaniel.armchair_management.model.record;

import java.math.BigDecimal;

public interface RowOccupacyProjection {
    Integer getFileira();
    Integer getTotalAssentos();
    Integer getAssentosLivres();
    BigDecimal getTaxaDesocupacaoPercentual();
}
