package shortages;

import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionAlgorithm {

    private final String productRefNo;
    private final List<LocalDate> dates;
    private final ProductionOutput outputs;
    private final Demand demandsPerDay;
    private final CurrentStock stock;

    public ShortagePredictionAlgorithm(String productRefNo, List<LocalDate> dates, ProductionOutput outputs, Demand demandsPerDay, CurrentStock stock) {
        this.productRefNo = productRefNo;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
        this.stock = stock;
    }

    public Shortage findShortages() {
        long level = stock.getLevel();

        Shortage.Builder builder = Shortage.builder(productRefNo);
        for (LocalDate day : dates) {
            Demand.DailyDemand demand = demandsPerDay.get(day);
            if (demand == null) {
                level += outputs.getOutput(day);
                continue;
            }
            long produced = outputs.getOutput(day);

            long levelOnDelivery = demand.calculateLevelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                builder.add(day, levelOnDelivery);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return builder.build();
    }
}
