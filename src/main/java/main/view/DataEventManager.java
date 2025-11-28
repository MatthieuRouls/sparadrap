package main.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataEventManager {

    private static DataEventManager instance;
    private final List<DataRefreshListener> listeners;

    public enum EventType {
        CLIENT_ADDED,
        CLIENT_UPDATED,
        CLIENT_DELETED,
        MEDECIN_ADDED,
        MEDECIN_UPDATED,
        MEDECIN_DELETED,
        MUTUELLE_ADDED,
        MUTUELLE_UPDATED,
        MUTUELLE_DELETED,
        MEDICAMENT_ADDED,
        MEDICAMENT_UPDATED,
        MEDICAMENT_DELETED,
        VENTE_COMPLETED,
        STOCK_UPDATED,
    }

    private DataEventManager() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public static DataEventManager getInstance() {
        if (instance == null) {
            instance = new DataEventManager();
        }
        return instance;
    }

    public void addListener(DataRefreshListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(DataRefreshListener listener) {
        listeners.remove(listener);
    }

    public void fireEvent(EventType eventType) {
        for (DataRefreshListener listener : listeners) {
            try {
                handleEvent(listener, eventType, null);
            } catch (Exception e) {
                System.err.println("Erreur lors de la notification d'evenement: " + e.getMessage());
            }
        }
    }

    public void fireEvent(EventType eventType, Object data) {
        for (DataRefreshListener listener : listeners) {
            try {
                handleEvent(listener, eventType, data);
            } catch (Exception e) {
                System.err.println("Erreur lors de la notification d'evenement: " + e.getMessage());
            }
        }
    }

    private void handleEvent(DataRefreshListener listener, EventType eventType, Object data) {
        switch (eventType) {
            case CLIENT_ADDED:
            case CLIENT_UPDATED:
            case CLIENT_DELETED:
                listener.refreshClientCount();
                break;

            case MEDECIN_ADDED:
            case MEDECIN_UPDATED:
            case MEDECIN_DELETED:
                if (listener instanceof PharmacieMainFrame) {
                    // Actualiser le compteur de médecins quand il existera
                    ((PharmacieMainFrame) listener).refreshMedecinCount();
                }
                break;

            case MUTUELLE_ADDED:
            case MUTUELLE_UPDATED:
            case MUTUELLE_DELETED:
                if (listener instanceof PharmacieMainFrame) {
                    // Notifier les panels qui utilisent les mutuelles (ClientPanel)
                    ((PharmacieMainFrame) listener).refreshMutuellesList();
                }
                break;

            case MEDICAMENT_ADDED:
            case MEDICAMENT_UPDATED:
            case MEDICAMENT_DELETED:
            case STOCK_UPDATED:
                listener.refreshStockCount();
                break;

            case VENTE_COMPLETED:
                if (data instanceof Integer) {
                    listener.refreshVenteCount((Integer) data);
                }
                if (data instanceof String) {
                    listener.refreshCaCount((String) data);
                }
                listener.refreshStockCount(); // Les ventes affectent le stock
                break;
        }
    }

    public void refreshALl() {
        for (DataRefreshListener listener : listeners) {
            try {
                listener.refreshAllCounts();
            } catch (Exception e) {
                System.err.println("Erreur lors de l'actualisation: " + e.getMessage());
            }
        }
    }

    public static class ClientEvents {
        public static void clientAdded() {
            getInstance().fireEvent(EventType.CLIENT_ADDED);
        }
        public static void clientUpdated() {
            getInstance().fireEvent(EventType.CLIENT_UPDATED);
        }
        public static void clientDeleted() {
            getInstance().fireEvent(EventType.CLIENT_DELETED);
        }
    }

    public static class MedicamentEvents {
        public static void medicamentAdded() {
            getInstance().fireEvent(EventType.MEDICAMENT_ADDED);
        }

        public static void medicamentUpdated() {
            getInstance().fireEvent(EventType.MEDICAMENT_UPDATED);
        }

        public static void medicamentDeleted() {
            getInstance().fireEvent(EventType.MEDICAMENT_DELETED);
        }

        public static void stockUpdated() {
            getInstance().fireEvent(EventType.STOCK_UPDATED);
        }
    }

    public static class VenteEvents {
        public static void venteCompleted(int nombreVentes, String chiffreAffaires) {
            getInstance().fireEvent(EventType.VENTE_COMPLETED, nombreVentes);
            // Notifier aussi le CA
            if (getInstance().listeners.size() > 0) {
                DataRefreshListener mainListener = getInstance().listeners.get(0);
                if (mainListener instanceof PharmacieMainFrame) {
                    mainListener.refreshCaCount(chiffreAffaires);
                }
            }
        }
    }

    public static class MutuelleEvents {
        public static void mutuelleAdded() {
            getInstance().fireEvent(EventType.MUTUELLE_ADDED);
        }
        public static void mutuelleUpdated() {
            getInstance().fireEvent(EventType.MUTUELLE_UPDATED);
        }
        public static void mutuelleDeleted() {
            getInstance().fireEvent(EventType.MUTUELLE_DELETED);
        }
    }
}

