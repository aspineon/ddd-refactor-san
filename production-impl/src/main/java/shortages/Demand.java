package shortages;

import entities.DemandEntity;
import enums.DeliverySchema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tools.Util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demand {

    private final Map<LocalDate, DemandEntity> demandsPerDay;

    public Demand(List<DemandEntity> demands) {
        demandsPerDay = new HashMap<>();
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }
    }

    public DailyDemand get(LocalDate day) {
        if (demandsPerDay.containsKey(day)) {
            return new DailyDemand(demandsPerDay.get(day));
        } else {
            return null;
        }
    }

    public static class DailyDemand {
        private final DemandEntity entity;

        public DailyDemand(DemandEntity entity) {
            this.entity = entity;
        }

        public long getLevel() {
            return Util.getLevel(entity);
        }

        private boolean isDeliverySchema(DeliverySchema schema) {
            return Util.getDeliverySchema(entity) == schema;
        }

        public long calculateLevelOnDelivery(long level, long produced) {
            long levelOnDelivery;
            if (isDeliverySchema(DeliverySchema.atDayStart)) {
                levelOnDelivery = level - getLevel();
            } else if (isDeliverySchema(DeliverySchema.tillEndOfDay)) {
                levelOnDelivery = level - getLevel() + produced;
            } else if (isDeliverySchema(DeliverySchema.every3hours)) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new NotImplementedException();
            } else {
                // TODO implement other variants
                throw new NotImplementedException();
            }
            return levelOnDelivery;
        }
    }
}
