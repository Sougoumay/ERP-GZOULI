# Frontend — ERP GZOULI

Application web Angular qui consomme l'API backend via CloudFront (`/api/*` en prod, `nginx` en Docker local).

---

## Stack technique

| Technologie | Version |
|-------------|---------|
| Angular | 21.2.x |
| Angular Material | 21.x |
| Node.js | 22 |
| npm | 11 |
| Authentification | amazon-cognito-identity-js |
| Éditeur riche | ngx-quill / Quill |

---

## Prérequis

- Node.js 22
- npm 11 (`npm install -g npm@11`)

---

## Installation et lancement

```bash
cd frontend

# Installer les dépendances
npm ci

# Lancer le serveur de développement
npm start
# → http://localhost:4200
```

Le serveur de développement recharge automatiquement à chaque modification de fichier.

---

## Build production

```bash
npm run build
# Artefacts dans : dist/front/browser/
```

Le build de production optimise les assets (tree-shaking, minification, hashing des noms de fichiers).

### Dockerfile

Build multi-stage : compilation avec `node:20-alpine` → service statique avec `nginx:stable-alpine`.

```bash
docker build -t gzouli-frontend .
docker run -p 80:80 gzouli-frontend
```

Le `nginx.conf` configure deux routes :
- `/*` → fichiers statiques Angular (`try_files $uri /index.html` pour le routing SPA)
- `/api/` → proxy vers `http://backend:8080` (utilisé uniquement en Docker Compose local)

---

## Configuration des environnements

| Fichier | Utilisé par |
|---------|-------------|
| `src/environments/environment.ts` | `ng serve` (dev local) |
| `src/environments/environment.prod.ts` | `npm run build` (prod) |

En production, `apiUrl: '/api'` est relatif — CloudFront route `/api/*` vers l'ALB sans configuration supplémentaire côté Angular.

---

## Authentification

L'authentification est gérée par `amazon-cognito-identity-js` qui communique directement avec AWS Cognito depuis le navigateur. Les tokens JWT obtenus sont envoyés dans l'header `Authorization: Bearer <token>` de chaque requête API.

Les paramètres Cognito (`userPoolId`, `clientId`) sont définis dans les fichiers d'environnement.

---

## Structure des sources

```
src/
├── app/
│   ├── core/           # Guards, intercepteurs HTTP, services auth
│   ├── shared/         # Composants réutilisables
│   ├── features/       # Modules fonctionnels (projets, employés, ...)
│   └── app.routes.ts   # Routing principal
├── environments/
│   ├── environment.ts
│   └── environment.prod.ts
└── assets/
    └── logo-gzouli.jpg
```

---

## Commandes utiles

| Commande | Description |
|----------|-------------|
| `npm start` | Serveur de dev (port 4200) |
| `npm run build` | Build production |
| `npm run watch` | Build dev en mode watch |
| `npm test` | Tests unitaires (Vitest) |
| `npx ng generate component <nom>` | Générer un composant |

---

## Notes de dépendances

Certaines dépendances sont fixées via `overrides` dans `package.json` pour corriger des CVEs détectées par Trivy :

| Package | Version fixée | CVE corrigée |
|---------|--------------|--------------|
| `js-cookie` | `3.0.8` | CVE-2026-46625 (transitive via Cognito) |
| `lodash-es` | `4.18.1` | CVE-2026-4800 (transitive via Quill) |

`@emnapi/core` et `@emnapi/runtime` sont ajoutés en `devDependencies` pour satisfaire les peer dependencies de `@napi-rs/wasm-runtime` (requis par Vitest/Rolldown).
