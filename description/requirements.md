

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


## Architecture
### Description préliminaire de l'architecture

Notre application sera composée d'un frontend permettant d'afficher et de faire tourner le jeu côté client, ainsi que d'un serveur backend qui servira de source unique de vérité, assurera la persistance des données nécessaires et gérera l'authentification des utilisateurs. Une base de données relationnelle sera également nécessaire afin de stocker les informations utiles au bon fonctionnement de l'application, comme les comptes utilisateurs. Un ORM sera par ailleurs utilisé afin de résoudre les problèmes de différence de structure entre les données manipulées par le backend et celles stockées en base de données (impedance mismatch). Enfin, un pipeline CI/CD permettra d'automatiser les tâches redondantes telles que les tests et le déploiement.

### Description des choix techniques

![architecture](Architecture.png)

Nous avons tout d'abord décidé de travailler avec le langage Java, ce qui a orienté l'ensemble de nos choix techniques.

Concernant le frontend, l'objectif était de choisir un outil permettant de faire le rendu du jeu. Plusieurs options existent, notamment des bibliothèques graphiques comme PixiJS, ou des moteurs de jeu (game engines) comme Phaser, Unity, Godot ou LibGDX. Par souci de simplicité, nous nous sommes orientés vers un moteur de jeu, qui met à disposition de nombreuses fonctionnalités telles qu'une boucle de gameplay et un système de physique, contrairement aux bibliothèques de rendu qui se limitent à l'affichage de sprites. Nous avons également souhaité privilégier un outil bénéficiant d'une large communauté, afin de disposer d'une documentation riche et de nombreux exemples facilitant son apprentissage. Enfin, comme mentionné précédemment, nous avons privilégié une solution permettant de coder en Java. Nous avons donc choisi le framework LibGDX, qui nous permet de faire le rendu du jeu en temps réel côté client, ainsi que de communiquer avec notre backend via HTTP (.NET), pour effectuer des requêtes CRUD ou établir des connexions WebSocket selon nos besoins.

Notre backend a pour rôle de valider les entrées utilisateur, de gérer les différents accès à nos endpoints, ainsi que l'authentification des utilisateurs et la persistance des données. Pour cela, nous avions le choix entre plusieurs frameworks tels que Quarkus, Spring Boot ou Play. Nous nous sommes orientés vers un framework que nous connaissions déjà et avec lequel nous avions de l'expérience : Quarkus, qui répond à l'ensemble de nos besoins.

Pour notre base de données, nous avons également opté pour une technologie que nous maîtrisions déjà, à savoir PostgreSQL comme SGBD. Pour faire le lien entre notre backend et notre base de données, nous avons choisi un ORM que nous avions déjà utilisé, Hibernate, plutôt que de nous tourner vers d'autres outils que nous ne connaissions pas mais qui auraient pu offrir un résultat similaire, comme JOOQ par exemple.

Nous avons choisi GitHub comme gestionnaire de versions plutôt que GitLab ou Bitbucket. C'est une solution qui nous semblait la plus simple à mettre en œuvre et avec laquelle nous avions déjà de l'expérience, notamment via GitHub Actions, que nous utiliserons pour mettre en place notre pipeline CI/CD. En ce qui concerne les tests nous allons utiliser Junit pour tester la logique de notre application et afin de s'assurer de la qualité du code nous allons utiliser Jacoco pour avoir un appercu du coverage des nos tests.

Enfin, afin de garantir un environnement reproductible et stable, nous avons décidé d'utiliser Docker et Docker Compose, permettant de lancer notre application et notre base de données de manière reproductible et coordonnée, facilitant ainsi le déploiement de l'application.
