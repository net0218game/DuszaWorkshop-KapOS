# Szerverkonfigurálási dokumentáció

## --------------------------------------------------------------------- 1. Szerver bérlés -------------------------------------------------------------------------------

#### Első és legfontosabb lépés, a számunkra megfelelő cloud hosting service megtalálása. 
#### Ehhez az alábbi szempontokat érdemes figyelembe venni:

  * Ár és számlázási módszerek
    - Szerverünk nem csak alkalmanként, hanem "non-stop" fut, ezért érdemes figyelembe vennünk a bérlési árakat. Az utóbbi indokok miatt érdemes olyan szolgáltatást, avagy csomagot választanunk, amely ezt lehetővé teszi.

  * Tárhely
    - Rengeteg beszélgetést, csatolt fájlt vagy profilképet kell eltárolnunk, ezért fontos szerepet játszik a bérelt tárhely, ahol az egész adatbázist eltároljuk.
  
  * Memória
    - Célunk, hogy egy publikusan elérhető webservert készítsünk, ezért kalkulálnunk kell a(z) (egyidejű) látogatókkal. Annak érdekében, hogy a szerveren futó folyamatok és a weboldalra látogatók igényei zökkenésmentesen zajlódjon, ezért több RAM-ra lesz szükségünk.



## ---------------------------------------------------------------------- 2. OS Config -----------------------------------------------------------------------------------

* Naprakész JDK és Maven verzió telepítés, az alapértelmezett JDK eltávolítása.
  - Fontos, hogy a projektünknek megfelelő verziójú JDK-t és Maven-t használjunk magán a szerveren is. Így kiküszöbölhetünk kellemetlen bug-okat, hosszas hibaelhárításokat, amelyek a használt JDK és Maven verziók eltéréséből adódhatnak.

* Python könyvtárak telepítése
   Használt Python könyvtárak: 
    - os
    - time
    - subprocess
    - sys
    - git
    - shutil
    - psutil

* Mappastruktúra kiépítése
  - ![This is an image](../DuszaWorkshop-KapOS/Dokumentumok/Képek/proj.png)

  - Hozzunk létre egy mappát, melyben a fontos fájlokat fogjuk eltárolni.
  - A mi pédánkban ez a mappa a ```./proj``` mappa lesz (Ennek az ominózus mappának a létrehozásához használjuk az ```mkdir proj``` parancsot).

  - A későbbiekben ebben a mappában fogjuk tárolni, például a(z):
    - application.properties fájlt, amelyet az eredetinek a felülírására használunk majd a későbbiekben
    - .jar fájlt
    - nohup.out fájlt, amely a .jar output-ját fogja tartalmazni
    - starter.log fájlt, amely a starter.py output-ját fogja tartalmazni.



## ----------------------------------------------------------------- 3. GitHub repository klónozása ----------------------------------------------------------------------

 - Fontos, hogy a verziókezelő rendszerünk segítségével szinkronba hozzuk a kódbázisunkat. Ennek érdekében klónozzuk a GitHub-on található repo-t. Ennek frissítését a későbbiekben automatizálni fogjuk (látsd: 4. Pont).

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
  - ahhoz, hogy mások egyszerűen eljuthassanal weboldalunkra, elhanyagolhatatlan egy domain cím. Mi a google domains oldalon vásároltunk magunknak egyet.
* felhőszolgáltatás összeköttetése a google dns-ekkel
  - A domain címet azonban össze is kell kötnünk a hosting szolgáltatónkkal, azaz a Digital Ocean-nel.

## 6. Tűzfal beállítások 
* Ezután a szerverünkön módosítanunk kell a tűzfalbeállításainkat, annak érdekében, hogy az újonnan vásárolt domain címünkről el is tudjuk érni az oldalunkat.
## 7. Port routing
* Végül át kell állítanunk az alapértelmezett portot, ahhoz, hogy ```www.hypechat.org:8080``` helyett elég legyen csak a ```www.hypechat.org```-ra rákeresni.