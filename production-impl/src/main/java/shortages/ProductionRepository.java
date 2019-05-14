package shortages;

import dao.ProductionDao;
import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.List;

public class ProductionRepository {
    private final ProductionDao dao;

    public ProductionRepository(ProductionDao dao) {
        this.dao = dao;
    }

    public ProductionOutput get(String productRefNo, LocalDate today) {
        List<ProductionEntity> productions = dao.findFromTime(productRefNo, today.atStartOfDay());

        return new ProductionOutput(productions);
    }
}
