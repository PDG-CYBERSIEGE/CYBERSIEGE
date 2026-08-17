

# Cahier des charges

## Introduction

## Besoins

### Fonctionnels

Menu
    - Au lancement de l'app, on arrive sur un menu
    - Un bouton permet de se connecter à un compte existant ou bien de s'en créer un

Gestion de compte
    - Chaque utilisateur possède un compte avec mail, username, et mot de passe
    
Trouver une partie 1v1 en ligne
    - Bouton pour lancer une partie en ligne
    - Attente d'un deuxième joueur si nécessaire (file d'attente)
    - Une fois deux joueurs présents, lancer la partie en passant à la phase construction
    
Phase 1 : construction
    - Obtention aléatoire d'un certains nombres de blocs à placer
    - Placement dans la zone constructible des blocs par le joueur via drag and drop
    - Placement dans la zone constructible du [ROI COCHON] par le joueur via drag and drop 
    - Placement impossible d'un bloc sur un autre bloc
    - Bouton pour tester l'intégrité de la structure
    - Bouton pour valider la structure
    - Une fois les structures des deux joueurs validées, passer à la phase de bataille
    - Au bout d'un certains temps, même si les structures ne sont pas validées, passer à la phase de bataille avec les structures actuelles
    - A la fin de la construction, le serveur valide la construction (nombre de blocs posés, positionnement, ...)

Phase 2 : bataille
    - Parrallèlement, en temps réel, les joueurs catapulte un [OISEAU] (de différents types) sur la base du joueur adverse
    - Les [OISEAUX] touchant des blocs sont ralentis/rebondissent en correspondance avec le type de bloc touché
    - Les [OISEAUX] disparaissent une fois que leur vitesse passe un certain seuil (proche de 0)
    - Pour lancer un nouvel [OISEAU], il faut que le dernier [OISEAU] ait disparu et qu'un délai dépendant du type de [OISEAU] soit passé
    - Les [OISEAUX] lancées font des dégats aux blocs de la base adverse qu'ils collisionnent
    - Les blocs colisionnés qui ont encore des pv sont simplement poussés mais s'ils n'ont plus de vie ils sont détruits
    - Les blocs se colisionant entre eux se font des dégats proprotionnel à la vitesse et au type du bloc colisionné
    - Un blocs tombant tout seul ne se fait pas de dégat à lui même
    - La victoire revient au premier joueur détruisant le [ROI COCHON] adverse
    - Le serveur valide chaque tire (oiseau dispo, délais entre les tirs, ...) et renvoie l'état de ses calculs pour que le client puisse actualiser son état si nécessaire

Fin de la partie
    - Une fois un joueur victorieux, un écran de victoire (ou de défaite) est affiché chez chacun des joueurs
    - L'écran de victoire/défaite permet de relancer une partie ou de retourner au menu


### Non fonctionnel
- Le serveur doit supporter 32 joueurs simultanés
- Le serveur se redémarre seul en cas de crash
- Le serveur doit gérer proprement la déconnection d'un joueur
- Le serveur doit être l'unique source de vérité
- Le nombre de rollback visible par manche doit être de maximum 2
- le delai maximum d'une requête ne doit pas dépasser 200 ms pour 95 % d'entre elles
- On doit au moins générer 30 images par secondes
- Les temps de chargements ne doivent pas dépasser 5 secondes
- Les mots de passes doivent être hashé grâce à une librairie digne des standarts de l'industrie
- le serveur doit être protegé contre les injections sql
- rediriger les requêtes http vers https