# 💫 About Us:
## <img width="32" height="32" alt="group" src="https://github.com/user-attachments/assets/408ca25f-d27a-40b2-98da-a667cb8175bc" /> Group Members : 
### - Ayoub Chmilla : https://github.com/chmillaayoub
### - Ilyass Oulhaj : https://github.com/IlyassOulhaj
### - Daoud Ahbachou : https://github.com/Thegoat1111-afk
### - Ali Anoya
### - Abderhmane Aroussi : https://github.com/Abderrahmane-Aroussi

# 🚀 Projet: Serveur Annuaire & Messagerie Simple (Java)

Ce projet est un serveur Java multi-threadé qui implémente un service "mixte" combinant un annuaire de contacts et un système de messagerie instantanée simple. Il utilise les Sockets pour la communication réseau et les Threads pour gérer plusieurs clients simultanément.

## 🎯 Objectif

L'objectif principal est de démontrer la gestion de multiples clients (concurrence) avec les Threads, tout en gérant un état partagé (l'annuaire et la liste des clients actifs) de manière sécurisée (thread-safe) à l'aide de `ConcurrentHashMap`.

## ✨ Fonctionnalités Principales

* **Serveur Multi-Threadé :** Capable de gérer plusieurs clients en parallèle. Chaque client est géré dans son propre `Thread` (`ClientHandler`).
* **Gestion d'Annuaire :** Stockage (en mémoire vive) des contacts (Nom, Téléphone, Email).
* **Messagerie Instantanée :** Permet aux clients *connectés* (authentifiés) d'envoyer des messages à d'autres clients *connectés*.
* **Authentification Simple :** Les utilisateurs doivent s'identifier (`LOGIN`) pour accéder aux fonctions de messagerie, mais peuvent consulter/ajouter à l'annuaire sans être loggés.
* **Communication TCP/IP :** Utilise une architecture client-serveur TCP/IP classique via `java.net.Socket`.
* **Support UTF-8 :** Les communications entre le client et le serveur sont encodées en UTF-8 pour supporter les caractères spéciaux (ex: accents français).

## 🛠️ Architecture et Technologies

* **Langage :** Java
* **Réseau :** `java.net.ServerSocket`, `java.net.Socket`
* **Concurrence :** `java.lang.Thread`
* **Collections Thread-Safe :** `java.util.concurrent.ConcurrentHashMap`

Le projet est structuré autour de 3 classes principales :

1.  **`AnnuaireServer.java` (Le Cerveau / Le "Patron")**
    * Contient la méthode `main()`.
    * Ouvre le `ServerSocket` et attend les connexions.
    * Pour chaque client qui se connecte, il crée et lance un nouveau `Thread` (`ClientHandler`).
    * Héberge les deux `ConcurrentHashMap` partagées :
        * `annuaire`: Stocke *tous* les contacts (Nom -> Objet Contact).
        * `activeClientHandlers`: Stocke *uniquement* les clients en ligne (Nom -> Objet ClientHandler).

2.  **`ClientHandler.java` (Le "Kheddam" / L'Ouvrier)**
    * Hérite de `Thread`.
    * Gère le cycle de vie complet d'un seul client.
    * Contient la boucle `while(true)` qui lit les commandes du client (`in.readLine()`).
    * Traite les commandes (`LOGIN`, `ADD`, `LIST`, `SEND_MSG`, `QUIT`).
    * Pour `SEND_MSG`, il recherche le `ClientHandler` du destinataire dans la map `activeClientHandlers` et appelle sa méthode `sendMessage()`.

3.  **`Contact.java` (L'Objet de Données)**
    * Une classe simple (POJO) pour stocker les informations d'un contact (nom, tel, email).

## ⌨️ Commandes Disponibles

Le serveur répond aux commandes textuelles suivantes (envoyées via un client Telnet ou un client Java) :

| Commande | Description | Exemple |
| :--- | :--- | :--- |
| **`ADD`** | Ajoute un nouveau contact à l'annuaire. | `ADD Ali 06... ali@...` |
| **`LIST`** | Affiche tous les contacts de l'annuaire. | `LIST` |
| **`LOGIN`** | S'identifie auprès du serveur. Nécessaire pour `SEND_MSG`. | `LOGIN Ali` |
| **`SEND_MSG`** | Envoie un message à un autre utilisateur *connecté*. | `SEND_MSG Bouchra Salam!` |
| **`QUIT`** | Met fin à la session du client. | `QUIT` |

## ⚙️ Comment Lancer et Tester

1.  **Compiler les fichiers :**
    ```bash
    javac AnnuaireServer.java ClientHandler.java Contact.java
    ```
2.  **Lancer le Serveur :**
    ```bash
    java AnnuaireServer
    ```
    *Le serveur devrait afficher : `🚀 Serveur (Annuaire + Messagerie) démarré...`*

3.  **Tester avec des Clients (Telnet) :**
    * Ouvrez **Terminal 1 (Client A)**:
        ```bash
        telnet localhost 12345
        ```
        *Réponse : `Bienvenue! Commandes dispo...`*
        ```
        ADD Ali 06... ali@...
        ADD Bouchra 07... bouchra@...
        LOGIN Ali
        ```

    * Ouvrez **Terminal 2 (Client B)**:
        ```bash
        telnet localhost 12345
        ```
        *Réponse : `Bienvenue! Commandes dispo...`*
        ```
        LOGIN Bouchra
        ```

    * Retournez au **Terminal 1 (Client A)**:
        ```
        SEND_MSG Bouchra Bonjour!
        ```
        *Réponse (Terminal A) : `OK: Message envoyé à Bouchra`*
        *Réponse (Terminal B) : `MESSAGE_FROM Ali: Bonjour!`*
# 📋 Project Report : 



# 👩‍🏫 Project Presentation : 

![](https://docs.google.com/presentation/d/1y6jm2PewqH9c8HOJOGu6MqF-2CH1t4oEocq9cmNZJ2k/edit?slide=id.p1#slide=id.p1)
      
# 💻 Tech Stack:
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

# ✍️ Random Dev Quote
![](https://quotes-github-readme.vercel.app/api?type=horizontal&theme=radical)

<!-- Proudly created with GPRM ( https://gprm.itsvg.in ) -->
