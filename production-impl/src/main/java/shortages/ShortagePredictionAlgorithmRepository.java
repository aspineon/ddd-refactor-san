package shortages;

import external.CurrentStock;
import external.StockService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortagePredictionAlgorithmRepository {

    ProductionOutputRepository productionOutputRepository;
    DemandRepository demandRepository;
    StockService stockService;

    public ShortagePredictionAlgorithm get(String productRefNo, LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        ProductionOutput outputs = productionOutputRepository.get(productRefNo, today);
        Demand demandsPerDay = demandRepository.findFrom(today.atStartOfDay(), productRefNo);
        CurrentStock stock = stockService.getCurrentStock(productRefNo);

        return new ShortagePredictionAlgorithm(productRefNo, dates, outputs, demandsPerDay, stock);
    }
}
