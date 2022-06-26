package it.gabrieletondi.telldontask.doubles;

import it.gabrieletondi.telldontask.domain.Product;
import it.gabrieletondi.telldontask.repository.ProductCatalog;

import java.util.List;

public class InMemoryProductCatalog implements ProductCatalog {
    private final List<Product> products;

    public InMemoryProductCatalog(List<Product> products) {
        this.products = products;
    }

    public Product getByName(final String name) {
        return products.stream()
                       .filter(product -> product.getName().equals(name))
                       .findFirst()
                       .orElse(null);
    }
}
