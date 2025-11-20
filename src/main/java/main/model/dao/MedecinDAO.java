package main.model.dao;

import main.model.Personne.CategoriePersonne.Medecin;
import main.model.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des Médecins dans la base de données.
 */
public class MedecinDAO {
    private static final Logger logger = LoggerFactory.getLogger(MedecinDAO.class);
    private final DatabaseConnection dbConnection;

    public MedecinDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Crée un nouveau médecin dans la base de données.
     */
    public void create(Medecin medecin) throws SQLException {
        logger.info("Création d'un nouveau médecin: {} {}", medecin.getPrenom(), medecin.getNom());

        String sql = "INSERT INTO medecins (identifiant, nom, prenom, adresse, code_postal, ville, " +
                     "telephone, email, numero_rpps) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medecin.getIdentifiant());
            stmt.setString(2, medecin.getNom());
            stmt.setString(3, medecin.getPrenom());
            stmt.setString(4, medecin.getAdresse());
            stmt.setString(5, medecin.getCodePostal());
            stmt.setString(6, medecin.getVille());
            stmt.setString(7, medecin.getNumTelephone());
            stmt.setString(8, medecin.getEmail());
            stmt.setString(9, medecin.getNumeroRPPS());

            int affectedRows = stmt.executeUpdate();
            logger.debug("Nombre de lignes affectées: {}", affectedRows);

            if (affectedRows > 0) {
                logger.info("Médecin '{}' créé avec succès (RPPS: {})", medecin.getNom(), medecin.getNumeroRPPS());
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la création du médecin '{}': {}", medecin.getNom(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère un médecin par son identifiant.
     */
    public Optional<Medecin> findByIdentifiant(String identifiant) throws SQLException {
        logger.debug("Recherche du médecin avec l'identifiant: {}", identifiant);

        String sql = "SELECT * FROM medecins WHERE identifiant = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifiant);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Medecin medecin = mapResultSetToMedecin(rs);
                    logger.debug("Médecin trouvé: {}", identifiant);
                    return Optional.of(medecin);
                }
            }

            logger.debug("Aucun médecin trouvé avec l'identifiant: {}", identifiant);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du médecin '{}': {}", identifiant, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère un médecin par son numéro RPPS.
     */
    public Optional<Medecin> findByRPPS(String numeroRPPS) throws SQLException {
        logger.debug("Recherche du médecin avec le RPPS: {}", numeroRPPS);

        String sql = "SELECT * FROM medecins WHERE numero_rpps = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroRPPS);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Medecin medecin = mapResultSetToMedecin(rs);
                    logger.debug("Médecin trouvé avec RPPS: {}", numeroRPPS);
                    return Optional.of(medecin);
                }
            }

            logger.debug("Aucun médecin trouvé avec le RPPS: {}", numeroRPPS);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du médecin par RPPS '{}': {}", numeroRPPS, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère tous les médecins.
     */
    public List<Medecin> findAll() throws SQLException {
        logger.debug("Récupération de tous les médecins");
        List<Medecin> medecins = new ArrayList<>();

        String sql = "SELECT * FROM medecins ORDER BY nom, prenom";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                medecins.add(mapResultSetToMedecin(rs));
            }

            logger.info("Nombre de médecins récupérés: {}", medecins.size());
            return medecins;

        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des médecins: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Met à jour un médecin existant.
     */
    public void update(Medecin medecin) throws SQLException {
        logger.info("Mise à jour du médecin: {}", medecin.getIdentifiant());

        String sql = "UPDATE medecins SET nom = ?, prenom = ?, adresse = ?, code_postal = ?, " +
                     "ville = ?, telephone = ?, email = ?, numero_rpps = ? WHERE identifiant = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medecin.getNom());
            stmt.setString(2, medecin.getPrenom());
            stmt.setString(3, medecin.getAdresse());
            stmt.setString(4, medecin.getCodePostal());
            stmt.setString(5, medecin.getVille());
            stmt.setString(6, medecin.getNumTelephone());
            stmt.setString(7, medecin.getEmail());
            stmt.setString(8, medecin.getNumeroRPPS());
            stmt.setString(9, medecin.getIdentifiant());

            int affectedRows = stmt.executeUpdate();
            logger.debug("Nombre de lignes mises à jour: {}", affectedRows);

            if (affectedRows > 0) {
                logger.info("Médecin '{}' mis à jour avec succès", medecin.getIdentifiant());
            } else {
                logger.warn("Aucun médecin trouvé pour la mise à jour: {}", medecin.getIdentifiant());
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du médecin '{}': {}", medecin.getIdentifiant(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Supprime un médecin par son identifiant.
     */
    public boolean delete(String identifiant) throws SQLException {
        logger.info("Suppression du médecin: {}", identifiant);

        String sql = "DELETE FROM medecins WHERE identifiant = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifiant);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Médecin '{}' supprimé avec succès", identifiant);
                return true;
            } else {
                logger.warn("Aucun médecin trouvé pour la suppression: {}", identifiant);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du médecin '{}': {}", identifiant, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mappe un ResultSet vers un objet Medecin.
     */
    private Medecin mapResultSetToMedecin(ResultSet rs) throws SQLException {
        return new Medecin(
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("adresse"),
            rs.getString("code_postal"),
            rs.getString("ville"),
            rs.getString("telephone"),
            rs.getString("email"),
            rs.getString("identifiant"),
            rs.getString("numero_rpps")
        );
    }
}
