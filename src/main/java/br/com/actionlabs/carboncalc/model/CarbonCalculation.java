package br.com.actionlabs.carboncalc.model;

import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document("carbonCalculation")
public class CarbonCalculation {
  @Id
  private String id;

  private String name;
  private String email;
  private String phoneNumber;
  private String uf;

  private Integer energyConsumption;
  private List<TransportationDTO> transportation = new ArrayList<>();
  private Integer solidWasteTotal;
  private Double recyclePercentage;
}
