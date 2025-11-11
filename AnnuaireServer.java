import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AnnuaireServer {
    
    private static final int PORT = 12345; 

   
    public static final Map<String, Contact> annuaire = new ConcurrentHashMap<>();

    
    public static final Map<String, Object> activeClientHandlers = new ConcurrentHashMap<>();
    
    
    public static String handleAddCommand(String[] parts) {
        if (parts.length != 4) {
            return "ERROR: Syntax ADD invalide. Utilisation: ADD Nom Tel Email";
        }
        
        String nom = parts[1];
        String telephone = parts[2];
        String email = parts[3];
        
        if (annuaire.containsKey(nom)) {
            return "ERROR: Le contact " + nom + " existe déjà dans l'annuaire.";
        } else {
            Contact nouveauContact = new Contact(nom, telephone, email);
            annuaire.put(nom, nouveauContact);
            return "OK: Le contact " + nom + " a été ajouté avec succès.";
        }
    }


    public static String handleListCommand() {
        if (annuaire.isEmpty()) {
            return "INFO: L'annuaire est actuellement vide.";
        }

        StringBuilder listResult = new StringBuilder();
        listResult.append("--- Liste des Contacts ---\n");
        
        for (Contact contact : annuaire.values()) {
            listResult.append(contact.toString()).append("\n");
        }
        listResult.append("--------------------------");
        
        return listResult.toString();
    }
    
    public static void main(String[] args) {
        System.out.println("Serveur Annuaire démarré sur le port " + PORT + "...");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            
            while (true) {
                Socket clientSocket = serverSocket.accept(); 
                System.out.println("\nNouveau client en attente de traitement: " + clientSocket.getInetAddress());

                
                handleSingleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println(" Erreur critique du serveur: " + e.getMessage());
        }
    }
    
    
    private static void handleSingleRequest(Socket clientSocket) {
         try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
         ) {
            String clientCommand = in.readLine();
            String response = "ERROR: Commande non traitée ou vide."; 

            if (clientCommand != null) {
                System.out.println("   -> Reçu: " + clientCommand);
                String[] parts = clientCommand.trim().split("\\s+", 4); 
                String commandType = parts[0].toUpperCase();

                switch (commandType) {
                    case "ADD":
                        response = handleAddCommand(parts);
                        break;
                    case "LIST":
                        response = handleListCommand();
                        break;
                    default:
                        response = "ERROR: Commande '" + commandType + "' inconnue.";
                        break;
                }
            }
            
            out.println(response);
            System.out.println("   <- Renvoyé: " + response.split("\n")[0] + "...");
            
        } catch (IOException e) {
            System.err.println("   ! Erreur de communication avec le client: " + e.getMessage());
        }
    }
}