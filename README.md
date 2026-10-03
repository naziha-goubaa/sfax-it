# Sfax IT

## Présentation

**Sfax IT** est une application mobile Android développée en Java dans le cadre d'un projet académique. Elle a pour objectif de faciliter la consultation et la gestion d'informations relatives aux entreprises du secteur informatique à Sfax.

L'application propose une interface mobile permettant aux utilisateurs de consulter les entreprises, d'accéder à leurs informations détaillées et d'utiliser différents services complémentaires tels que la météo, les paramètres de l'application et l'authentification.

## Fonctionnalités

* Authentification des utilisateurs

  * Inscription
  * Connexion
* Consultation des entreprises informatiques
* Affichage des informations détaillées d'une entreprise
* Ajout d'entreprises
* Gestion des données locales avec SQLite
* Recherche et consultation des informations disponibles
* Consultation de la météo
* Système de notation
* Gestion du profil et des paramètres
* Support de plusieurs langues
* Mode clair / sombre
* Interface adaptée à une utilisation mobile
* Consultation des logos et informations des entreprises référencées

## Entreprises référencées

L'application intègre notamment des informations et ressources graphiques associées à plusieurs entreprises du secteur IT, parmi lesquelles :

* ACTIA
* CALEN
* FOD
* Primatec
* Sofrecom
* Spark IT
* Udini

## Technologies utilisées

### Développement mobile

* **Java**
* **Android**
* **Android Studio**
* **XML**

### Données

* **SQLite**
* Base de données locale pour la gestion des utilisateurs et des informations des entreprises

### Services

* **OpenWeather API** pour les informations météorologiques
* API REST avec Retrofit pour la communication avec le service météo

### Outils

* **Gradle**
* **Git**
* **GitHub**

## Architecture du projet

Le projet est organisé selon une architecture Android classique avec séparation des principales responsabilités :

```text
Sfax IT/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com.example.applicationmobile5/
│           │       ├── AddCompanyActivity.java
│           │       ├── BaseActivity.java
│           │       ├── Company.java
│           │       ├── DatabaseHelper.java
│           │       ├── DetailsActivity.java
│           │       ├── LoginActivity.java
│           │       ├── MainActivity.java
│           │       ├── RegisterActivity.java
│           │       ├── SettingsActivity.java
│           │       ├── SplashActivity.java
│           │       ├── WeatherResponse.java
│           │       └── WeatherService.java
│           │
│           └── res/
│               ├── drawable/
│               ├── layout/
│               ├── mipmap/
│               └── values/
│
├── demo/
│   └── app.mp4
│
├── docs/
│   └── projet_evaluation.pdf
│
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── gradlew
└── gradlew.bat
```

## Base de données

L'application utilise une base de données **SQLite locale** afin de stocker les informations nécessaires au fonctionnement de l'application.

La classe `DatabaseHelper` assure notamment la gestion de la base de données et des opérations associées aux utilisateurs et aux entreprises.

Cette approche permet à l'application de gérer localement une partie importante de ses données sans dépendre d'une base de données distante.

## API météo

La fonctionnalité météo utilise une API REST accessible à travers **Retrofit**.

La communication avec le service est structurée à l'aide de :

* `WeatherService.java`
* `WeatherResponse.java`

La clé API n'est pas incluse dans le dépôt public. Une clé personnelle doit être configurée pour utiliser cette fonctionnalité.

## Interface utilisateur

L'interface Android est développée en **XML** et organisée autour de plusieurs activités, notamment :

* écran de démarrage ;
* connexion ;
* inscription ;
* accueil ;
* ajout d'une entreprise ;
* détails d'une entreprise ;
* paramètres.

L'application propose également un mode clair/sombre ainsi que plusieurs options de configuration.

## Démonstration

Une vidéo de démonstration de l'application est disponible dans le dossier :

```text
demo/app.mp4
```

## Documentation

La documentation du projet est disponible dans :

```text
docs/projet_evaluation.pdf
```

## Installation

### Prérequis

Avant de lancer le projet, installer :

* Android Studio
* JDK compatible avec la version du projet
* Android SDK
* Git

### Cloner le projet

```bash
git clone https://github.com/naziha-goubaa/sfax-it.git
```

Puis ouvrir le projet avec **Android Studio**.

### Configuration de l'API météo

Pour utiliser la fonctionnalité météo, créer/configurer une clé API OpenWeather personnelle et remplacer la valeur placeholder présente dans le projet par cette clé, selon la configuration utilisée dans le code.

Il est recommandé de ne jamais publier une clé API personnelle dans un dépôt GitHub public.

### Exécution

Après la synchronisation Gradle :

1. connecter un appareil Android ou démarrer un émulateur ;
2. sélectionner l'appareil dans Android Studio ;
3. lancer l'application avec **Run**.

## Gestion des versions

Le projet utilise **Git** pour le contrôle de version et est publié sur GitHub.

Repository :

https://github.com/naziha-goubaa/sfax-it

## Contexte académique

**Projet : Sfax IT**
**Plateforme : Android**
**Technologie principale : Java**
**Base de données : SQLite**
**Service externe : OpenWeather API**

## Auteure

**Naziha Goubaa**

GitHub :
https://github.com/naziha-goubaa
