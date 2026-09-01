# PDG-1v1

[Landing page](https://pdg-cybersiege.github.io/CYBERSIEGE/)

## Lancement de l'application

Pour lancer l'app vous devez avoir un personnal access token pour read:packages, 
si vous avez ca alors vous pourrez vous connecter avec
```cmd
docker login ghcr.io
```

une fois connecté avec votre username et votre clé vous pouvez

```cmd
docker compose pull
docker compose up
```

## lancement de la présentation

```cmd
cd présentation/reveal.js
npm install
npm start
```

## pour avoir la version en production
```yml
     server:
     image : ghcr.io/pdg-cybersiege/cybersiege:latest
     container_name: PDG_backend
```

## pour avoir la version sur votre pc 
```yml
  server:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: PDG_backend
```

## crée clé pour envoi JWT
a run dans server/src/main/ressources avec un terminal git bash (openssl déja installé) ou autre si openssl installé
```git bash
openssl genrsa -out privateKey.pem 2048
openssl rsa -in privateKey.pem -pubout -out publicKey.pem
```
