package shortages;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Shortage {

    private final String productRefNo;
    private final LocalDate foundAtDate;
    private final Map<LocalDate, Long> shortages;

    public Shortage(String productRefNo, LocalDate foundAtDate, Map<LocalDate, Long> shortages) {
        this.productRefNo = productRefNo;
        this.foundAtDate = foundAtDate;
        this.shortages = shortages;
    }

    public static Shortage.Builder builder(String productRefNo) {
        return new Shortage.Builder(productRefNo, LocalDate.now());
    }

    public static class Builder {
        private final String productRefNo;
        private final LocalDate now;
        private final Map<LocalDate, Long> shortages = new HashMap<>();

        public Builder(String productRefNo, LocalDate now) {
            this.productRefNo = productRefNo;
            this.now = now;
        }

        public void add(LocalDate day, long shortageAmount) {
            shortages.put(day, Math.abs(shortageAmount));
        }

        public Shortage build() {
            return new Shortage(productRefNo, now, shortages);
        }
    }
}
