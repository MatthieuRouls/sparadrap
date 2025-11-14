package main.view;

public interface DataRefreshListener {

    void refreshClientCount();

    void refreshMedecinCount();

    void refreshStockCount();

    void refreshVenteCount(int nombreVentes);

    void refreshCaCount(String caValue);

    default void refreshAllCounts() {
        refreshStockCount();
        refreshMedecinCount();
        refreshStockCount();
    }
}
