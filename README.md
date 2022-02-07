# MMORPG szakdolgozat

Bővebb leírás hamarosan...

## projekt fordítása

A projekt fordításához lépj be a projekt fő könyvtárába
és futtasd le a megfelelő scriptet. A kész artifact az "artifacts" könyvtárába fog kerülni.

fordításhoz szükséges előre telepített programok:
 - backend: jdk 11+
 - frontend: nodejs

## projekt fordítása dockerrel
A projekt dockerrel történő fordításához lépj be a projekt fő könyvtárába és futtasd le az alábbi parancsok közül a megfelelőt. A kész artifact az "artifacts" könyvtárába fog kerülni.

frontend fordítása:
 - docker container run --rm -it -w /repo -v $(pwd):/repo node:16 /bin/sh ./build_frontend.sh

backend fordítása:
 - docker container run --rm -it -w /repo -v $(pwd):/repo openjdk:11 /bin/sh ./build_backend.sh
