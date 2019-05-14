package shortages;

import dao.DemandDao;
import entities.DemandEntity;

import java.time.LocalDateTime;
import java.util.List;

public class DemandRepository {

    private DemandDao demandDao;

    public Demand findFrom(LocalDateTime atStartOfDay, String productRefNo) {
        List<DemandEntity> demands = demandDao.findFrom(atStartOfDay, productRefNo);
        return new Demand(demands);
    }
}
