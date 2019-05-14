package shortages;

import java.time.LocalDate;
import java.util.Map;

public class ProductionOutput {

    private final Map<LocalDate, Long> outputs;

    public ProductionOutput(Map<LocalDate, Long> outputs) {
        this.outputs = outputs;

    }

    public long getOutput(LocalDate day) {
        return outputs.getOrDefault(day, 0L);
    }
}
