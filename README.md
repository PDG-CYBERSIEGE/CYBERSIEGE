# PDG-1v1

[Landing page](https://pdg-cybersiege.github.io/CYBERSIEGE/)

## Lancement de l'application

Pour lancer l'app vous devez avoir un personnal access token pour read:packages, 
si vous avez ca alors vous pourrez vous connecter avec
```cmd
docker login ghcr.io
```

une fois connecter avec votre username et votre clé vous pouvez

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