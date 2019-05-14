package shortages;

import dao.ProductionDao;
import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionOutputRepository {
    private final ProductionDao dao;

    public ProductionOutputRepository(ProductionDao dao) {
        this.dao = dao;
    }

    public ProductionOutput get(String productRefNo, LocalDate today) {
        List<ProductionEntity> productions = dao.findFromTime(productRefNo, today.atStartOfDay());

        return new ProductionOutput(productions
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getStart().toLocalDate(),
                        ProductionEntity::getOutput
                )));
    }
}
