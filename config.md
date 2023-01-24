# Szerverkonfigurálási dokumentáció

## 1. Szerver bérlés
#### Első, és legfontosabb lépés a számunkra megfelelő cloud hosting service megtalálása. Ehhez az alábbi szempontokat érdemes figyelembe venni:
* Ár, számlázási módszerek
  - Mivel szerverünk nem csak alkalmanként, hanem non-stop futni fog, érdemes számításba venni az árakat. Olyan szolgáltatást, vagy csomagot érdemes választani, amely ezt lehetővé teszi.
* Tárhely
  - Adatbázisunk mérete miatt fontos szerepet játszik a tárhely is, mivel rengeteg beszélgetést, csatolt fájlt, vagy profilképet kell eltárolnunk.
* Memnória
  - A célunk az az, hogy egy publikusan elérhető webservert készítsünk, ezért kalkulálni kell a látogatókkal. Annak érdekében hogy a szerveren zajló folyamatokat, és a látogatók igényeit is ki tudjuk szolgálni, elég sok RAM-ra lesz szükségünk.


## 2. OS Config

* Projektnek megfelelő JDK, és maven telepítése, az alapértelmezett JDK eltávolítása.
  - Fontos, hogy a projektünknek megfelelő verziójú JDK-t, és maven-t használjunk a szerveren is. Így elkerülhetünk kellemetlen bug-okat, melyek a használt verziók eltéréséből adódhatnak.
* python konyvtárak telepítése
  - Használt python konyvtárak: 
    - os
    - time
    - subprocess
    - sys
    - git
    - shutil
    - psutil
* mappastruktúra kiépítése
  - ![This is an image](../DuszaWorkshop-KapOS/Dokumentumok/Képek/proj.png)
  - Hozzunk létre egy mappát, melyben a fontos fájlokat fogjuk tárolni.
  - A mi pédánkban ez a mappa a ```./proj``` mappa lesz. (létrehozásához használd az ```mkdir proj``` parancsot)
  - A későbbiekben ebben a mappában fogjuk tárolni, például a(z):
    - application.properties fájlt, amit az eredeti felülírására használunk majd  
    - .jar fájlunkat
    - nohup.out fájlt, mely a .jar output-ját fogja tartalmazni
    - starter.log fájlt, mely a starter.py output-ját fogja tartalmazni.

## 3. GitHub repository klónozása

#### Fontos, hogy a verziókövető rendszerünk segítségével szinkronba hozzuk a kódbázisunkat. Ennek érdekében klónozzuk a GitHub-on található repo-t. Ennek frissítését a későbbiekben automatizálni fogjuk (látsd: 4. Pont).

## 4. Szerver folyamatok automatizálása - starter.py

#### Mivel nem szeretnénk minden egyes változtatás után a szerveren manuálisan elvégezni a teendőket, ezt automatizáltuk. Minden GitHub változás után a szerveren található kódbázis frissül a változtatott verzióra.

### Starter.py működési elve

* github repo valtozatasok ellenőrzése, pullolása

* Ha nem volt változtatás:
  - Fut-e a jar file? 
    - Ha igen: kilepes a script-ből
    - Ha nem: .jar file futtatása nohuppal
* Ha volt változtatás a github repoban:
  - git pull
  - .jar folyamat leállítása
  - eredeti .jar törlése ./proj mappából
  - .jar átmásolása ./proj mappába
  - futtatás nohuppal
    - ```nohup java -jar hypeDemo-0.0.1-SNAPSHOT.jar &"```
### Meghatározott időközönként futtatás crontabbal
#### Ahhoz, hogy garantáni tudjuk a szünetmentes, naprakész kiszolgálást, a lehető legtöbb alkalommal vizsgáljuk a szerveren futó kód naprakészségét. Ezt crontab-al érjük el, amely 5 percenként elindítja a starter.py scriptünket.

crontab parancs:

```*/5 * * * * python /opt/DuszaWorkshop-KapOS/tutorial/hypeDemo/src/main/resources/starter.py >/proj/starter.log 2>&1```

## 5. Domain cím
* google domains domain cím vásárlása
* felhőszolgáltatás összeköttetése a google dns-ekkel

## 6. Tűzfal beállítások 
## 7. Port routing