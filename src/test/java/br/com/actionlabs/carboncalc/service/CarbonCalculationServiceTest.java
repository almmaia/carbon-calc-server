package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.model.CarbonCalculation;
import br.com.actionlabs.carboncalc.model.EnergyEmissionFactor;
import br.com.actionlabs.carboncalc.model.SolidWasteEmissionFactor;
import br.com.actionlabs.carboncalc.model.TransportationEmissionFactor;
import br.com.actionlabs.carboncalc.repository.CarbonCalculationRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarbonCalculationServiceTest {

  @Mock private CarbonCalculationRepository carbonCalculationRepository;
  @Mock private EnergyEmissionFactorRepository energyEmissionFactorRepository;
  @Mock private TransportationEmissionFactorRepository transportationEmissionFactorRepository;
  @Mock private SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;

  @InjectMocks private CarbonCalculationService service;

  @Test
  void startCalcShouldCreateCalculationAndReturnId() {
    StartCalcRequestDTO request = new StartCalcRequestDTO();
    request.setName("Alan");
    request.setEmail("alan@example.com");
    request.setPhoneNumber("11999999999");
    request.setUf("mg");

    CarbonCalculation saved = new CarbonCalculation();
    saved.setId("abc123");
    when(carbonCalculationRepository.save(any(CarbonCalculation.class))).thenReturn(saved);

    StartCalcResponseDTO response = service.startCalc(request);

    assertThat(response.getId()).isEqualTo("abc123");

    ArgumentCaptor<CarbonCalculation> captor = ArgumentCaptor.forClass(CarbonCalculation.class);
    verify(carbonCalculationRepository).save(captor.capture());
    assertThat(captor.getValue().getUf()).isEqualTo("MG");
  }

  @Test
  void updateInfoShouldOverwriteDataForExistingCalculation() {
    UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
    request.setId("calc-1");
    request.setEnergyConsumption(100);
    request.setTransportation(List.of());
    request.setSolidWasteTotal(50);
    request.setRecyclePercentage(0.25);

    CarbonCalculation calculation = new CarbonCalculation();
    calculation.setId("calc-1");
    calculation.setUf("MG");
    when(carbonCalculationRepository.findById("calc-1")).thenReturn(Optional.of(calculation));
    when(carbonCalculationRepository.save(any(CarbonCalculation.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UpdateCalcInfoResponseDTO response = service.updateInfo(request);

    assertThat(response.isSuccess()).isTrue();
    verify(carbonCalculationRepository).save(calculation);
    assertThat(calculation.getEnergyConsumption()).isEqualTo(100);
    assertThat(calculation.getSolidWasteTotal()).isEqualTo(50);
    assertThat(calculation.getRecyclePercentage()).isEqualTo(0.25);
  }

  @Test
  void getResultShouldCalculateAllEmissions() {
    CarbonCalculation calculation = new CarbonCalculation();
    calculation.setId("calc-1");
    calculation.setUf("MG");
    calculation.setEnergyConsumption(100);
    calculation.setTransportation(List.of(buildTransportation(TransportationType.CAR, 10)));
    calculation.setSolidWasteTotal(50);
    calculation.setRecyclePercentage(0.25);

    EnergyEmissionFactor energy = new EnergyEmissionFactor();
    energy.setUf("MG");
    energy.setFactor(0.53);

    TransportationEmissionFactor transportation = new TransportationEmissionFactor();
    transportation.setType(TransportationType.CAR);
    transportation.setFactor(0.19);

    SolidWasteEmissionFactor waste = new SolidWasteEmissionFactor();
    waste.setUf("MG");
    waste.setRecyclableFactor(0.45);
    waste.setNonRecyclableFactor(0.95);

    when(carbonCalculationRepository.findById("calc-1")).thenReturn(Optional.of(calculation));
    when(energyEmissionFactorRepository.findById("MG")).thenReturn(Optional.of(energy));
    when(transportationEmissionFactorRepository.findById(TransportationType.CAR)).thenReturn(Optional.of(transportation));
    when(solidWasteEmissionFactorRepository.findById("MG")).thenReturn(Optional.of(waste));

    CarbonCalculationResultDTO result = service.getResult("calc-1");

    assertThat(result.getEnergy()).isEqualTo(53.0);
    assertThat(result.getTransportation()).isEqualTo(1.9);
    assertThat(result.getSolidWaste()).isEqualTo(41.25);
    assertThat(result.getTotal()).isEqualTo(96.15);
  }

  @Test
  void getResultShouldRejectMissingId() {
    assertThrows(Exception.class, () -> service.getResult(" "));
  }

  private TransportationDTO buildTransportation(TransportationType type, int distance) {
    TransportationDTO dto = new TransportationDTO();
    dto.setType(type);
    dto.setMonthlyDistance(distance);
    return dto;
  }
}
