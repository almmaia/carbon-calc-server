package br.com.actionlabs.carboncalc.rest;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.service.CarbonCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Slf4j
public class OpenRestController {
  private final CarbonCalculationService carbonCalculationService;

  @PostMapping("start-calc")
  public ResponseEntity<StartCalcResponseDTO> startCalculation(
      @RequestBody StartCalcRequestDTO request) {
    return ResponseEntity.ok(carbonCalculationService.startCalc(request));
  }

  @PutMapping("info")
  public ResponseEntity<UpdateCalcInfoResponseDTO> updateInfo(
      @RequestBody UpdateCalcInfoRequestDTO request) {
    return ResponseEntity.ok(carbonCalculationService.updateInfo(request));
  }

  @GetMapping("result/{id}")
  public ResponseEntity<CarbonCalculationResultDTO> getResult(@PathVariable String id) {
    return ResponseEntity.ok(carbonCalculationService.getResult(id));
  }
}
