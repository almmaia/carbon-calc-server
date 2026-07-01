package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.model.CarbonCalculation;
import br.com.actionlabs.carboncalc.model.EnergyEmissionFactor;
import br.com.actionlabs.carboncalc.model.SolidWasteEmissionFactor;
import br.com.actionlabs.carboncalc.model.TransportationEmissionFactor;
import br.com.actionlabs.carboncalc.repository.CarbonCalculationRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonCalculationService {
  private final CarbonCalculationRepository carbonCalculationRepository;
  private final EnergyEmissionFactorRepository energyEmissionFactorRepository;
  private final TransportationEmissionFactorRepository transportationEmissionFactorRepository;
  private final SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;

  public StartCalcResponseDTO startCalc(StartCalcRequestDTO request) {
    validateStartRequest(request);

    CarbonCalculation calculation = new CarbonCalculation();
    calculation.setName(request.getName().trim());
    calculation.setEmail(request.getEmail().trim());
    calculation.setPhoneNumber(request.getPhoneNumber().trim());
    calculation.setUf(request.getUf().trim().toUpperCase());
    calculation = carbonCalculationRepository.save(calculation);

    StartCalcResponseDTO response = new StartCalcResponseDTO();
    response.setId(calculation.getId());
    return response;
  }

  public UpdateCalcInfoResponseDTO updateInfo(UpdateCalcInfoRequestDTO request) {
    if (request == null || request.getId() == null || request.getId().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
    }
    if (request.getEnergyConsumption() < 0 || request.getSolidWasteTotal() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "consumption values must be non-negative");
    }
    if (request.getRecyclePercentage() < 0.0 || request.getRecyclePercentage() > 1.0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recyclePercentage must be between 0 and 1.0");
    }

    CarbonCalculation calculation = getCalculationOrThrow(request.getId());
    calculation.setEnergyConsumption(request.getEnergyConsumption());
    calculation.setTransportation(request.getTransportation() == null ? List.of() : request.getTransportation());
    calculation.setSolidWasteTotal(request.getSolidWasteTotal());
    calculation.setRecyclePercentage(request.getRecyclePercentage());
    carbonCalculationRepository.save(calculation);

    UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();
    response.setSuccess(true);
    return response;
  }

  public CarbonCalculationResultDTO getResult(String id) {
    CarbonCalculation calculation = getCalculationOrThrow(id);
    log.info("Loaded calculation id={} uf={} energyConsumption={} transportation={} solidWasteTotal={} recyclePercentage={}",
        id, calculation.getUf(), calculation.getEnergyConsumption(), calculation.getTransportation(),
        calculation.getSolidWasteTotal(), calculation.getRecyclePercentage());

    double energy = 0.0;
    if (calculation.getEnergyConsumption() != null) {
      EnergyEmissionFactor factor = energyEmissionFactorRepository.findById(calculation.getUf())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "energy factor not found"));
      log.info("Energy factor uf={} factor={}", factor.getUf(), factor.getFactor());
      energy = calculation.getEnergyConsumption() * factor.getFactor();
    }

    double transportation = 0.0;
    if (calculation.getTransportation() != null) {
      for (TransportationDTO item : calculation.getTransportation()) {
        if (item == null || item.getType() == null) {
          continue;
        }
        if (item.getMonthlyDistance() < 0) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transportation distance must be non-negative");
        }
        TransportationEmissionFactor factor = transportationEmissionFactorRepository.findById(item.getType())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transportation factor not found"));
        log.info("Transportation factor type={} factor={} distance={}", factor.getType(), factor.getFactor(), item.getMonthlyDistance());
        transportation += item.getMonthlyDistance() * factor.getFactor();
      }
    }

    double solidWaste = 0.0;
    if (calculation.getSolidWasteTotal() != null) {
      SolidWasteEmissionFactor factor = solidWasteEmissionFactorRepository.findById(calculation.getUf())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "solid waste factor not found"));
      log.info("Waste factor uf={} recyclable={} nonRecyclable={}", factor.getUf(), factor.getRecyclableFactor(),
          factor.getNonRecyclableFactor());
      double recyclableShare = calculation.getRecyclePercentage() == null ? 0.0 : calculation.getRecyclePercentage();
      if (recyclableShare < 0.0 || recyclableShare > 1.0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recyclePercentage must be between 0 and 1.0");
      }
      double nonRecyclableShare = 1.0 - recyclableShare;
      solidWaste = calculation.getSolidWasteTotal()
          * (recyclableShare * factor.getRecyclableFactor() + nonRecyclableShare * factor.getNonRecyclableFactor());
    }

    CarbonCalculationResultDTO response = new CarbonCalculationResultDTO();
    response.setEnergy(energy);
    response.setTransportation(transportation);
    response.setSolidWaste(solidWaste);
    response.setTotal(energy + transportation + solidWaste);
    return response;
  }

  private CarbonCalculation getCalculationOrThrow(String id) {
    if (id == null || id.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
    }
    return carbonCalculationRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "calculation not found"));
  }

  private void validateStartRequest(StartCalcRequestDTO request) {
    if (request == null || isBlank(request.getName()) || isBlank(request.getEmail())
        || isBlank(request.getPhoneNumber()) || isBlank(request.getUf())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name, email, phoneNumber and uf are required");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
