import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName = null; 

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Bienvenue! Commandes dispo: LOGIN Nom | ADD Nom Tel Email | LIST | QUIT");

            String clientCommand;
            while ((clientCommand = in.readLine()) != null) {
                
                
                String[] parts = clientCommand.trim().split("\\s+", 4); 
                String commandType = parts[0].toUpperCase();
                String response = "ERROR: Commande inconnue.";

            

                if (this.clientName == null) {
                    
                    System.out.println("   [Client ??] Reçu: " + clientCommand);
                    
                    switch (commandType) {
                        case "LOGIN":
                            
                            response = handleLoginCommand(clientCommand.trim().split("\\s+", 2));
                            break;
                        case "ADD":
                            
                            response = AnnuaireServer.handleAddCommand(parts);
                            break;
                        case "LIST":
                            response = AnnuaireServer.handleListCommand();
                            break;
                        case "SEND_MSG":
                            
                            response = "ERROR: Identification requise. Utilisez LOGIN Nom d'abord.";
                            break;
                        case "QUIT":
                            out.println("OK: Déconnexion.");
                            return; 
                        default:
                            response = "ERROR: Commande '" + commandType + "' inconnue. (Pas connecté)";
                            break;
                    }
                } else {
                    
                    System.out.println("   [Client " + clientName + "] Reçu: " + clientCommand);
                    
                    String[] sendParts = clientCommand.trim().split("\\s+", 3);

                    switch (commandType) {
                        case "LOGIN":
                            response = "ERROR: Vous êtes déjà connecté en tant que " + this.clientName;
                            break;
                        case "ADD":
                            response = AnnuaireServer.handleAddCommand(parts);
                            break;
                        case "LIST":
                            response = AnnuaireServer.handleListCommand();
                            break;
                        case "SEND_MSG":
                            response = handleSendCommand(sendParts);
                            break;
                        case "QUIT":
                            out.println("OK: Déconnexion.");
                            return; 
                        default:
                            response = "ERROR: Commande '" + commandType + "' inconnue. (Connecté)";
                            break;
                    }
                }
                
                out.println(response);
            }
            
        } catch (IOException e) {
        } finally {
            try {
                if (this.clientName != null) {
                    AnnuaireServer.activeClientHandlers.remove(this.clientName);
                    System.out.println("   <- LOGOUT: " + this.clientName + " est hors ligne.");
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    private String handleLoginCommand(String[] parts) {
        if (parts.length != 2) {
            return "ERROR: Syntax LOGIN invalide. Utilisation: LOGIN Nom";
        }
        
        String nom = parts[1];
        
        if (!AnnuaireServer.annuaire.containsKey(nom)) {
            return "ERROR: Le contact '" + nom + "' n'existe pas dans l'annuaire. Utilisez ADD d'abord.";
        }

        if (AnnuaireServer.activeClientHandlers.containsKey(nom)) {
            return "ERROR: Le contact '" + nom + "' est déjà connecté.";
        }
        
        this.clientName = nom;
        AnnuaireServer.activeClientHandlers.put(this.clientName, this);
        System.out.println("   -> LOGIN: " + this.clientName + " est maintenant en ligne.");
        
        return "OK: Vous êtes connecté en tant que " + this.clientName + ". (SEND_MSG est dispo)";
    }

    private String handleSendCommand(String[] parts) {
        if (parts.length != 3) {
            return "ERROR: Syntax SEND_MSG invalide. Utilisation: SEND_MSG NomRecepteur Message";
        }
        
        String destinataire = parts[1];
        String message = parts[2];
        String expediteur = this.clientName; 

        if (!AnnuaireServer.annuaire.containsKey(destinataire)) {
            return "ERROR: Le contact '" + destinataire + "' n'existe pas dans l'annuaire.";
        }

        ClientHandler targetHandler = AnnuaireServer.activeClientHandlers.get(destinataire);

        if (targetHandler != null) {
            targetHandler.sendMessage("MESSAGE_FROM " + expediteur + ": " + message);
            return "OK: Message envoyé à " + destinataire;
        } else {
            return "ERROR: Le contact '" + destinataire + "' est hors ligne (offline).";
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}