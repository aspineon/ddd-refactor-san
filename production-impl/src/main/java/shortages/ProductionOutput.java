package shortages;

import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionOutput {

    private final Map<LocalDate, ProductionEntity> outputs;

    public ProductionOutput(List<ProductionEntity> productions) {
        outputs = new HashMap<>();
        for (ProductionEntity production : productions) {
            outputs.put(production.getStart().toLocalDate(), production);
        }
    }

    public long getOutput(LocalDate day) {
        ProductionEntity production = outputs.get(day);
        if (production != null) {
            return production.getOutput();
        }
        return 0;
    }
}
