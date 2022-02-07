# MMORPG szakdolgozat

Bővebb leírás hamarosan...

## projekt fordítása

A projekt fordításához lépj be a projekt fő könyvtárába és futtasd le a **build.sh** scriptet. A kész artifact a projekt **artifacts** könyvtárába fog kerülni. A projekt lefordítható dockerrel is (pl. ci-cd rendszerekben) a **--docker** flag használatával

A nem-dockerrel történő frodításhoz szükség van néhány előre telepített programra:
 - backend: jdk 11+
 - frontend: nodejs