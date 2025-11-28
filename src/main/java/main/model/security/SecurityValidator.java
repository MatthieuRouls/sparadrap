package main.model.security;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

public final class SecurityValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[1-9][0-9]{8}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^(?:(?:[0-8][0-9])|(?:9[0-5])|(?:2[AB]))[0-9]{3}$");
    private static final Pattern SECU_SOCIALE_PATTERN = Pattern.compile("^[12][0-9]{14}$");
    private static final Pattern RPPS_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern IDENTIFIANT_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{3,20}$");
    private static final Pattern MEDICAMENT_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9\\s\\-()]{2,50}$");
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("^[A-Z0-9]{3,15}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s'\\-]{2,30}$");
    private static final Pattern CITY_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s\\-]{2,50}$");

    public static final double MIN_PRIX = 0.00;
    public static final double MAX_PRIX = 10000.0;
    public static final int MIN_STOCK = 0;
    public static final int MAX_STOCK = 100000;
    public static final int MIN_QUANTITE = 1;
    public static final int MAX_QUANTITE = 1000;
    public static final double MIN_TAUX_REMBOURSEMENT = 0.0;
    public static final double MAX_TAUX_REMBOURSEMENT = 100.0;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SecurityValidator() {
        throw new UnsupportedOperationException("Classe utilitaire: ne peut pas être instanciée");
    }


    // ======== METHODES DE VALIDATION ========

    public static String validateAndTrimString(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " ne peut pas etre null");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " ne peut pas etre vide");
        }
        return trimmed;
    }

    public static String validateEmail(String email) {
        String trimmedEmail = validateAndTrimString(email, "Email");
        String lowerEmail = trimmedEmail.toLowerCase();

        if (!EMAIL_PATTERN.matcher(lowerEmail).matches()) {
            throw new IllegalArgumentException("Format d'email invalide : " + email);
        }
        return lowerEmail;
    }

    public static String validatePhoneNumber(String phone) {
        String trimmedPhone = validateAndTrimString(phone, "Numero de telephone");
        String cleanPhone = trimmedPhone.replaceAll("[\\s.\\-]", "");

        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new IllegalArgumentException("Numero de telephone invalide : " + phone + " (format attendu : 0xxxxxxxxx)");
        }
        return cleanPhone;
    }

    public static String validatePostalCode(String postalCode) {
        String trimmedCode = validateAndTrimString(postalCode, "Code Postal");

        if (!POSTAL_CODE_PATTERN.matcher(trimmedCode).matches()) {
            throw new IllegalArgumentException("Code Postal invalide : " + postalCode +
                    "'. Format attendu : 5 chiffres (ex: 75001, 2A001, 97400).");
        }
        return trimmedCode;
    }

    public static String validateNumeroSecuriteSociale(String numero) {
        String trimmedNumero = validateAndTrimString(numero, "Numero de Securite sociale");
        String cleanNumero = trimmedNumero.replaceAll("\\s", "");

        if (!SECU_SOCIALE_PATTERN.matcher(cleanNumero).matches()) {
            throw new IllegalArgumentException("Numero de Securite sociale invalide" );
        }
        return cleanNumero;
    }

    /**
     * Valide un numéro de sécurité sociale optionnel (peut être null ou vide).
     * @param numero Le numéro à valider
     * @return Le numéro nettoyé ou null si vide/null
     */
    public static String validateNumeroSecuriteSocialeOptional(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return null;
        }
        try {
            return validateNumeroSecuriteSociale(numero);
        } catch (IllegalArgumentException e) {
            // Si le format est invalide, on retourne le numéro tel quel
            // plutôt que de crasher l'application
            return numero.trim();
        }
    }

    public static String validateNumeroRPPS(String rpps) {
        String trimmedRPPS = validateAndTrimString(rpps, "Numero RPPS");
        String cleanRPPS = trimmedRPPS.replaceAll("\\s", "");

        if (!RPPS_PATTERN.matcher(cleanRPPS).matches()) {
            throw new IllegalArgumentException("Numero RPPS invalide : " + rpps);
        }
        return cleanRPPS;
    }

    public static String validateIdentifiant(String identifiant) {
        String trimmedId = validateAndTrimString(identifiant, "Identifiant");

        if (!IDENTIFIANT_PATTERN.matcher(trimmedId).matches()) {
            throw new IllegalArgumentException("Identifiant invalide : " + identifiant);
        }

        return trimmedId;
    }

    public static String validateMedicamentName(String name) {
        String trimmedName = validateAndTrimString(name, "Nom du médicament");

        if (!MEDICAMENT_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException("Nom de médicament invalide : " + name);
        }

        return trimmedName;
    }

    public static String validateReference(String reference) {
        String trimmedRef = validateAndTrimString(reference, "Référence");
        String upperRef = trimmedRef.toUpperCase();

        if (!REFERENCE_PATTERN.matcher(upperRef).matches()) {
            throw new IllegalArgumentException("Référence invalide : " + reference);
        }

        return upperRef;
    }

    public static String validatePersonName(String name, String fieldName) {
        String trimmedName = validateAndTrimString(name, fieldName);

        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException(fieldName + " invalide : " + name);
        }

        return trimmedName;
    }

    public static String validateCity(String city) {
        String trimmedCity = validateAndTrimString(city, "Ville");

        if (!CITY_PATTERN.matcher(trimmedCity).matches()) {
            throw new IllegalArgumentException("Nom de ville invalide : " + city);
        }

        return trimmedCity;
    }

// ========= VALIDATION NUMERIQUE ===========

    public static double validatePrix(double prix) {
        if (Double.isNaN(prix) || Double.isInfinite(prix)) {
            throw new IllegalArgumentException("Prix invalide : valeur non numerique");
        }
        if (prix < MIN_PRIX || prix > MAX_PRIX) {
            throw new IllegalArgumentException("Prix invalide : " + prix + " (entre " + MIN_PRIX + ", " + MAX_PRIX + "€)");
        }
        return prix;
    }

    public static int validateQuantite(int quantite) {
        if (quantite < MIN_QUANTITE || quantite > MAX_QUANTITE) {
            throw new IllegalArgumentException("Quantité invalide : " + quantite);
        }
        return quantite;
    }

    public static int validateStock(int stock) {
        if (stock < MIN_STOCK || stock > MAX_STOCK) {
            throw new IllegalArgumentException("Stock invalide : " + stock);
        }
        return stock;
    }

    public static double validateTauxRemboursement(double taux) {
        if (Double.isNaN(taux) || Double.isInfinite(taux)) {
            throw new IllegalArgumentException("Taux de remboursement invalide");
        }
        if (taux < MIN_TAUX_REMBOURSEMENT || taux > MAX_TAUX_REMBOURSEMENT) {
            throw new IllegalArgumentException("Taux de remboursement invalide" + taux + "%");
        }
        return taux;
    }

    // ============ VALIDATION DATES =============

    public static Date validateDate(Date date, String fieldName) {
        if (date == null) {
            throw new IllegalArgumentException(fieldName + " ne peut pas etre null");
        }
        return new Date(date.getTime());
    }

    public static Date validateFutureDate(Date date, String fieldName) {
        Date validateDate = validateDate(date, fieldName);
        Date now = new Date();

        if (!validateDate.after(now)) {
            throw new IllegalArgumentException(fieldName + " doit etre dans le futur");
        }
        return validateDate;
    }

    public static void validateDateOrder(Date dateDebut, Date dateFin, String fieldNameDebut, String fieldNameFin) {
        validateDate(dateDebut, fieldNameDebut);
        validateDate(dateFin, fieldNameFin);

        if (!dateDebut.before(dateFin)) {
            throw new IllegalArgumentException(fieldNameDebut + " doit etre anterieure a " + fieldNameFin);
        }
    }

    // ============ VALIDATION OBJETS ============

    public static <T> T validateNotNull(T object, String fieldName) {
        if (object == null) {
            throw new IllegalArgumentException(fieldName + " ne peut pas etre null");
        }
        return object;
    }

    public static <T extends Collection<?>> T validateCollection(T collection, String fieldName) {
        validateNotNull(collection, fieldName);
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " ne peut pas etre vide");
        }
        return collection;
    }

    // ============== SECURISATION DONNEES ===========

    public static String maskSecuriteSociale(String numeroComplet) {
        if (numeroComplet == null || numeroComplet.trim().isEmpty()) {
            return "Non renseigné";
        }
        try {
            String cleanNumero = validateNumeroSecuriteSociale(numeroComplet);
            return cleanNumero.charAt(0) + "*".repeat(13) + cleanNumero.charAt(cleanNumero.length() - 1);
        } catch (IllegalArgumentException e) {
            return "***";
        }

    }

    public static String maskEmail(String email) {
        try {
            String validEmail = validateEmail(email);
            String[] parts = validEmail.split("@");
            String localPart  = parts[0];
            String domainPart = parts[1];

            if (localPart.length() <= 1) {
                return "*@*" + domainPart;
            } else {
                return localPart.charAt(0) + "*".repeat(localPart.length() - 1) + "@" + domainPart;
            }
        } catch (IllegalArgumentException e) {
            return "***";
        }
    }

    // ============ METHODES METIER ============

    public static void validateStockOperation(int stockActuel, int quantiteDemandee, String operation) {
        validateStock(stockActuel);
        validateQuantite(quantiteDemandee);

        if ("REDUCTION".equals(operation) && stockActuel < quantiteDemandee) {
            throw new IllegalArgumentException(operation + "Stock insuffisant. Disponible: " + stockActuel +
                                                            ", Demande: " + quantiteDemandee);
        }
        if ("ADDITION".equals(operation) && stockActuel > Integer.MAX_VALUE - quantiteDemandee) {
            throw new IllegalArgumentException("Risque de debordement lors de l'ajout au stock");
        }
    }

    public static void validateMedicamentNotExpired(Date dateperemption, String nomMedicament) {
        validateDate(dateperemption, "Date de peremption");
        Date now = new Date();

        if (now.after(dateperemption)) {
            throw new IllegalArgumentException("Le medicament " + nomMedicament + " est perime");
        }
    }

    public static void validateTransaction(double montantTotal, double montantRembourse) {
        validatePrix(montantTotal);

        if (montantRembourse < 0) {
            throw new IllegalArgumentException("Le montant rembourse ne peut pas etre negatif");
        }
        if (montantRembourse > montantTotal) {
            throw new IllegalArgumentException("Le montant rembourse ne peut pas etre superieur au montant total");
        }
    }
}
