# CYBERSIEGE

CYBERSIEGE est un jeu en ligne multijoueur 1v1 développé dans le cadre du Projet de Groupe (PDG) à la HEIG-VD.

Le jeu oppose deux joueurs dans plusieurs manches. Chaque joueur construit une structure pour protéger son roi, puis attaque la structure adverse à l'aide de différents robots lancés avec une catapulte.

## Liens

- [Landing page](https://pdg-cybersiege.github.io/CYBERSIEGE/)



# Installation et lancement

## Prérequis

Pour lancer le projet localement, il est nécessaire d'avoir :

- Git
- Docker
- Docker Compose

Un Personal Access Token (PAT) GitHub avec la permission `read:packages` est uniquement nécessaire pour utiliser l'image Docker publiée sur GitHub Container Registry.

## Lancement en local

Pour construire le backend à partir du code source local, utiliser dans `docker-compose.yml` :

```yaml
server:
  build:
    context: .
    dockerfile: Dockerfile
  container_name: PDG_backend
```
Puis lancer :
```bash
docker compose up --build
```
Aucun Personal Access Token GitHub n'est nécessaire dans ce mode.

## Lancement avec l'image publiée (production)

Pour utiliser l'image Docker publiée sur GitHub Container Registry, utiliser dans `docker-compose.yml` :
```yaml
server:
image: ghcr.io/pdg-cybersiege/cybersiege:latest
container_name: PDG_backend
```
Il faut alors se connecter à GHCR avant de lancer l'application :
```bash
docker login ghcr.io
```
Le compte GitHub doit disposer d'un Personal Access Token avec la permission read:packages.

Puis :
```bash
docker compose pull
docker compose up
```

## Génération des clés JWT

Les clés utilisées pour signer et vérifier les JWT doivent être générées avant le lancement du serveur.

Se placer dans : ```server/src/main/resources```

Puis exécuter les commandes suivantes avec OpenSSL :
```
openssl genrsa -out privateKey.pem 2048
openssl rsa -in privateKey.pem -pubout -out publicKey.pem
```
La clé privée est utilisée pour signer les JWT et la clé publique pour les vérifier.
La clé privée ne doit jamais être commitée dans le repository.


# Structure du projet
Le projet est organisé en plusieurs parties correspondant aux différents composants de l'application.

- ```frontend/``` contient le client du jeu développé avec LibGDX. Il contient notamment la logique d'affichage, les écrans du jeu, les interactions avec le joueur et la communication avec le serveur via HTTP et WebSocket.

- ```server/``` contient le backend développé avec Quarkus. Il gère l'authentification, les comptes utilisateurs, le matchmaking, les parties et la logique serveur du jeu. Le serveur valide les actions des joueurs et maintient l'état de la partie.

- ```présentation/``` contient la présentation du projet réalisée avec Reveal.js.

- ```docker-compose.yml``` décrit les différents services nécessaires au fonctionnement de l'application et permet de les démarrer ensemble. Il permet notamment de choisir entre l'utilisation d'une image Docker déjà construite et publiée sur GitHub Container Registry ou la construction du backend directement à partir du code source local.

- ```Dockerfile``` décrit les étapes nécessaires pour construire l'image Docker du backend. Il permet de créer une image reproductible contenant l'application Quarkus et son environnement d'exécution.

## Fonctionnement général

Lorsqu'un joueur utilise l'application, le frontend s'exécute dans son navigateur et communique avec le backend.

Les communications HTTP sont utilisées pour les opérations classiques telles que l'authentification et la gestion des données. Les WebSockets sont utilisés pour les communications temps réel nécessaires au déroulement des parties.

Le backend communique avec PostgreSQL pour stocker les données persistantes, notamment les informations des utilisateurs.


# Contribution

## Développement

Les nouvelles fonctionnalités sont développées sur une branche dédiée créée à partir de `developpement`. Cette branche sert de branche d'intégration, tandis que `main` est réservée aux versions finales.

Pour créer une branche :

```bash
git checkout developpement
git pull
git checkout -b feature/nom-de-la-fonctionnalite
```

Pendant le développement, les nouvelles fonctionnalités doivent être accompagnées des tests nécessaires et respecter le formatage du projet avec Spotless.

Avant de pousser les modifications :

```bash
./gradlew spotlessCheck
./gradlew test
```

Si nécessaire, le formatage peut être appliqué automatiquement :

```bash
./gradlew spotlessApply
```

Une fois les vérifications terminées :
```bash
git add .
git commit -m "Add: description de la fonctionnalité"
git push -u origin feature/nom-de-la-fonctionnalite
```

## Pull Request et CI/CD

Une Pull Request est créée de feature/nom-de-la-fonctionnalite vers developpement.

Les GitHub Actions exécutent automatiquement les vérifications du projet, notamment les tests et le formatage. La Pull Request peut être fusionnée uniquement après validation et réussite des vérifications.

Après intégration et lors d'une release, le pipeline construit et publie l'image Docker du backend sur GitHub Container Registry (GHCR).

## Contribution au projet

Pour contribuer au projet :
- Créer une branche feature/* à partir de developpement.
- Développer la fonctionnalité et ajouter les tests nécessaires.
- Vérifier le formatage avec Spotless et exécuter les tests.
- Pousser la branche et créer une Pull Request vers developpement.
- Vérifier que les GitHub Actions passent.
- Faire valider la Pull Request pour la fusionner.

Les développements directs sur main ne sont pas autorisés.