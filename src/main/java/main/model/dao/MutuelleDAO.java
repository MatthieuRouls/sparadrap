package main.model.dao;

import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des Mutuelles dans la base de données.
 */
public class MutuelleDAO {
    private static final Logger logger = LoggerFactory.getLogger(MutuelleDAO.class);
    private final DatabaseConnection dbConnection;

    public MutuelleDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Crée une nouvelle mutuelle dans la base de données.
     */
    public void create(Mutuelle mutuelle) throws SQLException {
        logger.info("Création d'une nouvelle mutuelle: {}", mutuelle.getNom());

        String sql = "INSERT INTO mutuelles (nom, adresse, code_postal, ville, telephone, email, taux_remboursement) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, mutuelle.getNom());
            stmt.setString(2, mutuelle.getAdresse());
            stmt.setString(3, mutuelle.getCodePostal());
            stmt.setString(4, mutuelle.getVille());
            stmt.setString(5, mutuelle.getTelephone());
            stmt.setString(6, mutuelle.getEmail());
            stmt.setDouble(7, mutuelle.getTauxRemboursement());

            int affectedRows = stmt.executeUpdate();
            logger.debug("Nombre de lignes affectées: {}", affectedRows);

            if (affectedRows > 0) {
                logger.info("Mutuelle '{}' créée avec succès", mutuelle.getNom());
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la création de la mutuelle '{}': {}", mutuelle.getNom(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère une mutuelle par son nom.
     */
    public Optional<Mutuelle> findByNom(String nom) throws SQLException {
        logger.debug("Recherche de la mutuelle: {}", nom);

        String sql = "SELECT * FROM mutuelles WHERE nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Mutuelle mutuelle = mapResultSetToMutuelle(rs);
                    logger.debug("Mutuelle trouvée: {}", nom);
                    return Optional.of(mutuelle);
                }
            }

            logger.debug("Aucune mutuelle trouvée avec le nom: {}", nom);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de la mutuelle '{}': {}", nom, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère toutes les mutuelles.
     */
    public List<Mutuelle> findAll() throws SQLException {
        logger.debug("Récupération de toutes les mutuelles");
        List<Mutuelle> mutuelles = new ArrayList<>();

        String sql = "SELECT * FROM mutuelles ORDER BY nom";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                mutuelles.add(mapResultSetToMutuelle(rs));
            }

            logger.info("Nombre de mutuelles récupérées: {}", mutuelles.size());
            return mutuelles;

        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des mutuelles: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Met à jour une mutuelle existante.
     */
    public void update(Mutuelle mutuelle) throws SQLException {
        logger.info("Mise à jour de la mutuelle: {}", mutuelle.getNom());

        String sql = "UPDATE mutuelles SET adresse = ?, code_postal = ?, ville = ?, " +
                     "telephone = ?, email = ?, taux_remboursement = ? WHERE nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mutuelle.getAdresse());
            stmt.setString(2, mutuelle.getCodePostal());
            stmt.setString(3, mutuelle.getVille());
            stmt.setString(4, mutuelle.getTelephone());
            stmt.setString(5, mutuelle.getEmail());
            stmt.setDouble(6, mutuelle.getTauxRemboursement());
            stmt.setString(7, mutuelle.getNom());

            int affectedRows = stmt.executeUpdate();
            logger.debug("Nombre de lignes mises à jour: {}", affectedRows);

            if (affectedRows > 0) {
                logger.info("Mutuelle '{}' mise à jour avec succès", mutuelle.getNom());
            } else {
                logger.warn("Aucune mutuelle trouvée pour la mise à jour: {}", mutuelle.getNom());
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la mutuelle '{}': {}", mutuelle.getNom(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Supprime une mutuelle par son nom.
     */
    public boolean delete(String nom) throws SQLException {
        logger.info("Suppression de la mutuelle: {}", nom);

        String sql = "DELETE FROM mutuelles WHERE nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Mutuelle '{}' supprimée avec succès", nom);
                return true;
            } else {
                logger.warn("Aucune mutuelle trouvée pour la suppression: {}", nom);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la mutuelle '{}': {}", nom, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mappe un ResultSet vers un objet Mutuelle.
     */
    private Mutuelle mapResultSetToMutuelle(ResultSet rs) throws SQLException {
        return new Mutuelle(
            rs.getString("nom"),
            rs.getString("adresse"),
            rs.getString("code_postal"),
            rs.getString("ville"),
            rs.getString("telephone"),
            rs.getString("email"),
            rs.getDouble("taux_remboursement")
        );
    }
}
