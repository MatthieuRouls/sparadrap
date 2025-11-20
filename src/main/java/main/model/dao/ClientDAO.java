package main.model.dao;

import main.model.Organisme.TypeOrganisme.Mutuelle;
import main.model.Personne.CategoriePersonne.Client;
import main.model.Personne.CategoriePersonne.Medecin;
import main.model.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour la gestion des Clients dans la base de données.
 */
public class ClientDAO {
    private static final Logger logger = LoggerFactory.getLogger(ClientDAO.class);
    private final DatabaseConnection dbConnection;
    private final MutuelleDAO mutuelleDAO;
    private final MedecinDAO medecinDAO;

    public ClientDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.mutuelleDAO = new MutuelleDAO();
        this.medecinDAO = new MedecinDAO();
    }

    /**
     * Crée un nouveau client dans la base de données.
     */
    public void create(Client client) throws SQLException {
        logger.info("Création d'un nouveau client: {} {}", client.getPrenom(), client.getNom());

        String sql = "INSERT INTO clients (identifiant, nom, prenom, adresse, code_postal, ville, " +
                     "telephone, email, numero_secu, mutuelle_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getIdentifiant());
            stmt.setString(2, client.getNom());
            stmt.setString(3, client.getPrenom());
            stmt.setString(4, client.getAdresse());
            stmt.setString(5, client.getCodePostal());
            stmt.setString(6, client.getVille());
            stmt.setString(7, client.getNumTelephone());
            stmt.setString(8, client.getEmail());
            stmt.setString(9, client.getNumeroSecuriteSocial());

            // Récupérer l'ID de la mutuelle si elle existe
            if (client.getMutuelle() != null) {
                Integer mutuelleId = getMutuelleId(client.getMutuelle().getNom());
                if (mutuelleId != null) {
                    stmt.setInt(10, mutuelleId);
                } else {
                    stmt.setNull(10, Types.INTEGER);
                }
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            logger.debug("Nombre de lignes affectées: {}", affectedRows);

            if (affectedRows > 0) {
                logger.info("Client '{}' créé avec succès", client.getIdentifiant());
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la création du client '{}': {}", client.getIdentifiant(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère un client par son identifiant.
     */
    public Optional<Client> findByIdentifiant(String identifiant) throws SQLException {
        logger.debug("Recherche du client avec l'identifiant: {}", identifiant);

        String sql = "SELECT c.*, m.nom as mutuelle_nom, m.adresse as mutuelle_adresse, " +
                     "m.code_postal as mutuelle_cp, m.ville as mutuelle_ville, " +
                     "m.telephone as mutuelle_tel, m.email as mutuelle_email, " +
                     "m.taux_remboursement as mutuelle_taux " +
                     "FROM clients c " +
                     "LEFT JOIN mutuelles m ON c.mutuelle_id = m.id " +
                     "WHERE c.identifiant = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifiant);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = mapResultSetToClient(rs);
                    logger.debug("Client trouvé: {}", identifiant);
                    return Optional.of(client);
                }
            }

            logger.debug("Aucun client trouvé avec l'identifiant: {}", identifiant);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du client '{}': {}", identifiant, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère tous les clients.
     */
    public List<Client> findAll() throws SQLException {
        logger.debug("Récupération de tous les clients");
        List<Client> clients = new ArrayList<>();

        String sql = "SELECT c.*, m.nom as mutuelle_nom, m.adresse as mutuelle_adresse, " +
                     "m.code_postal as mutuelle_cp, m.ville as mutuelle_ville, " +
                     "m.telephone as mutuelle_tel, m.email as mutuelle_email, " +
                     "m.taux_remboursement as mutuelle_taux " +
                     "FROM clients c " +
                     "LEFT JOIN mutuelles m ON c.mutuelle_id = m.id " +
                     "ORDER BY c.nom, c.prenom";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }

            logger.info("Nombre de clients récupérés: {}", clients.size());
            return clients;

        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des clients: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Met à jour un client existant.
     */
    public void update(Client client) throws SQLException {
        logger.info("Mise à jour du client: {}", client.getIdentifiant());

        String sql = "UPDATE clients SET nom = ?, prenom = ?, adresse = ?, code_postal = ?, " +
                     "ville = ?, telephone = ?, email = ?, numero_secu = ?, mutuelle_id = ? " +
                     "WHERE identifiant = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getAdresse());
            stmt.setString(4, client.getCodePostal());
            stmt.setString(5, client.getVille());
            stmt.setString(6, client.getNumTelephone());
            stmt.setString(7, client.getEmail());
            stmt.setString(8, client.getNumeroSecuriteSocial());

            // Récupérer l'ID de la mutuelle si elle existe
            if (client.getMutuelle() != null) {
                Integer mutuelleId = getMutuelleId(client.getMutuelle().getNom());
                if (mutuelleId != null) {
                    stmt.setInt(9, mutuelleId);
                } else {
                    stmt.setNull(9, Types.INTEGER);
                }
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.setString(10, client.getIdentifiant());

            int affectedRows = stmt.executeUpdate();
            logger.debug("Nombre de lignes mises à jour: {}", affectedRows);

            if (affectedRows > 0) {
                logger.info("Client '{}' mis à jour avec succès", client.getIdentifiant());
            } else {
                logger.warn("Aucun client trouvé pour la mise à jour: {}", client.getIdentifiant());
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du client '{}': {}", client.getIdentifiant(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Supprime un client par son identifiant.
     */
    public boolean delete(String identifiant) throws SQLException {
        logger.info("Suppression du client: {}", identifiant);

        String sql = "DELETE FROM clients WHERE identifiant = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifiant);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Client '{}' supprimé avec succès", identifiant);
                return true;
            } else {
                logger.warn("Aucun client trouvé pour la suppression: {}", identifiant);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du client '{}': {}", identifiant, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère l'ID d'une mutuelle par son nom.
     */
    private Integer getMutuelleId(String nom) throws SQLException {
        String sql = "SELECT id FROM mutuelles WHERE nom = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

            return null;

        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de l'ID de la mutuelle '{}': {}", nom, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mappe un ResultSet vers un objet Client.
     */
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        // Récupérer la mutuelle si elle existe
        Mutuelle mutuelle = null;
        String mutuelleNom = rs.getString("mutuelle_nom");
        if (mutuelleNom != null) {
            mutuelle = new Mutuelle(
                mutuelleNom,
                rs.getString("mutuelle_adresse"),
                rs.getString("mutuelle_cp"),
                rs.getString("mutuelle_ville"),
                rs.getString("mutuelle_tel"),
                rs.getString("mutuelle_email"),
                rs.getDouble("mutuelle_taux")
            );
        }

        // Pour l'instant, on ne récupère pas le médecin traitant (null)
        // On pourrait l'implémenter plus tard si nécessaire
        return new Client(
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("adresse"),
            rs.getString("code_postal"),
            rs.getString("ville"),
            rs.getString("telephone"),
            rs.getString("email"),
            rs.getString("identifiant"),
            rs.getString("numero_secu"),
            mutuelle,
            null // medecinTraitant - pas géré pour l'instant
        );
    }
}
