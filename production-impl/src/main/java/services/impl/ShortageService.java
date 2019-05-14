package services.impl;

import dao.DemandDao;
import dao.ProductionDao;
import dao.ShortageDao;
import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import external.StockService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tools.Util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortageService {

    private DemandDao demandDao;
    private ShortageDao shortageDao;
    private StockService stockService;
    private ProductionDao productionDao;

    /**
     * Production at day of expected delivery is quite complex:
     * We are able to produce and deliver just in time at same day
     * but depending on delivery time or scheme of multiple deliveries,
     * we need to plan properly to have right amount of parts ready before delivery time.
     * <p/>
     * Typical schemas are:
     * <li>Delivery at prod day start</li>
     * <li>Delivery till prod day end</li>
     * <li>Delivery during specified shift</li>
     * <li>Multiple deliveries at specified times</li>
     * Schema changes the way how we calculate shortages.
     * Pick of schema depends on customer demand on daily basis and for each product differently.
     * Some customers includes that information in callof document,
     * other stick to single schema per product. By manual adjustments of demand,
     * customer always specifies desired delivery schema
     * (increase amount in scheduled transport or organize extra transport at given time)
     * <p>
     * TODO algorithm is finding wrong shortages, when more productions is planned in a single day
     */
    public List<ShortageEntity> findShortages(String productRefNo, LocalDate today, int daysAhead) {

        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay());
        Map<LocalDate, ProductionEntity> outputs = new HashMap<>();
        for (ProductionEntity production : productions) {
            outputs.put(production.getStart().toLocalDate(), production);
        }
        List<DemandEntity> demands = demandDao.findFrom(today.atStartOfDay(), productRefNo);
        Map<LocalDate, DemandEntity> demandsPerDay = new HashMap<>();
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        long level = stock.getLevel();

        List<ShortageEntity> shortages = new LinkedList<>();
        for (LocalDate day : dates) {
            DemandEntity demand = demandsPerDay.get(day);
            if (demand == null) {
                ProductionEntity production = outputs.get(day);
                if (production != null) {
                    level += production.getOutput();
                }
                continue;
            }
            long produced = 0;
            ProductionEntity production = outputs.get(day);
            if (production != null) {
                produced = production.getOutput();
            }

            long levelOnDelivery;
            if (Util.getDeliverySchema(demand) == DeliverySchema.atDayStart) {
                levelOnDelivery = level - Util.getLevel(demand);
            } else if (Util.getDeliverySchema(demand) == DeliverySchema.tillEndOfDay) {
                levelOnDelivery = level - Util.getLevel(demand) + produced;
            } else if (Util.getDeliverySchema(demand) == DeliverySchema.every3hours) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new NotImplementedException();
            } else {
                // TODO implement other variants
                throw new NotImplementedException();
            }

            if (!(levelOnDelivery >= 0)) {
                ShortageEntity entity = new ShortageEntity();
                entity.setRefNo(productRefNo);
                entity.setFound(LocalDate.now());
                entity.setMissing(levelOnDelivery * -1L);
                entity.setAtDay(day);
                shortages.add(entity);
            }
            long endOfDayLevel = level + produced - Util.getLevel(demand);
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        shortageDao.delete(productRefNo);
        shortageDao.saveAll(shortages);

        return shortages;
    }

    private ShortageService() {
    }
}
