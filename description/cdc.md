# Cahier des charges


## Introduction

### Contexte

Ce projet est réalisé dans le cadre de l’unité d’enseignement Projet de groupe (PDG) à la HEIG-VD. Cette unité a pour objectif, à travers la réalisation en équipe d’une application de taille importante, de mettre en pratique les compétences acquises durant la formation ainsi qu'acquérir de manière indépendante des connaissances sur des sujets nouveaux.

### Objectif

#### Principe du jeu

L’objectif du projet est de développer un jeu multijoueur en ligne 1VS1 inspiré du principe d’Angry Birds. Deux joueurs s’affrontent dans une partie composée de plusieurs manches, le premier joueur à en remporter la majorité remportant la partie. Chaque manche se déroule en deux phases : une phase de construction, durant laquelle chaque joueur crée une structure destinée à protéger son [Roi cochon], puis une phase de bataille, durant laquelle les joueurs utilisent différents types [d’oiseaux] qu’ils catapultent afin de tenter de détruire la structure adverse. Le premier joueur à détruire le [Roi cochon] adverse remporte la manche.

Lors de la phase de construction, les joueurs disposent de différents types de blocs, chacun possédant des caractéristiques propres qui influencent leur comportement lors des collisions. Pendant la phase de bataille, les collisions subies par les blocs leur infligent des dégâts en fonction du type et de la vitesse de l’impact. Leurs points de vie diminuent en conséquence et un bloc est détruit lorsque ceux-ci atteignent zéro.

#### Réalisation

La réalisation du jeu nécessite notamment la mise en place d’une simulation physique (aidée par un framework spécialisé) permettant de gérer les mouvements des oiseaux et des blocs, ainsi que les collisions et leurs conséquences. Cette simulation doit prendre en compte les caractéristiques des différents éléments afin de reproduire de manière cohérente leurs interactions.

Le jeu repose également sur un serveur responsable de la gestion de l’état des parties en validant les actions effectuées par les joueurs. Il constitue ainsi la source de vérité du jeu et maintient un état cohérent entre les deux clients, notamment lors des échanges temps réel.

Enfin, le projet comprend la gestion des comptes utilisateurs, l’authentification, la mise en place d’un système de file d'attente pour trouver une partie et la gestion des parties jusqu’à avoir un gagnant.


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
    - En cas de déconnection, le joueur restant gagne la partie


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
