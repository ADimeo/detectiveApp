# App für das Detective Game

Ein Projekt im Rahmen des Game Programming Seminars am HPI im Wintersemester 2019.

Dieses Readme beschreibt nur die App. Eine komplette Projektbeschreibung, sowie Installationsanweisungen für den Server befinden sich im [Server Repo](https://github.com/EatingBacon/gameprog-detective-server/)

### App Setup
Um die App zu starten, muss erst das neuste [Release](https://github.com/ADimeo/gameprog-detective-app/releases) heruntergeladen und [installiert werden](https://www.androidpit.com/android-for-beginners-what-is-an-apk-file#howto) (App und Server Version müssen übereinstimmen.  
Damit die App funktioniert muss die Telegram App installiert sein.

### Verwendung
*Zu Beginn ist der Sicherheitsmodus aktiviert. Dieser verhindert das Hochladen von persönlichen Daten (über http), macht das Spiel aber unspielbar (Siehe [Spielen ohne freigeben persönlicher Daten](https://github.com/EatingBacon/gameprog-detective-server/#spielen-ohne-freigeben-pers%C3%B6nlicher-daten)). Der Sicherheitsmodus kann in den Einstellungen der App deaktiviert werden*

Nach dem ersten Öffnen der App gelangt man durch den Klick auf *Kommissar kontaktieren* in einen neuen Telegram Chat.
Der Kommissar gibt immer wieder Aufgaben, welche in der App gelöst werden können.
Die App wird beim Klick auf Aufgaben eine Nachricht geben, ob diese erfolgreich gelöst wurde, oder ob ein Fehler auftrat.

### Einstellungen
**Server URL**: Ändert die Server URL (funktioniert dynamisch, aber es muss möglicherweise resetet werden, da der neue Server nicht den alten Benutzer erkennt)   
**Safety Mode**: Falls an wird die App keine Daten hochladen und Aufgaben können nicht beendet werden
**Phone Number**: Falls die App die Nummer nicht richtig erkannt hat, kann sie hier geändert werden
**Reset**: Löscht alle Daten des momentanen Benutzers im Server und in der App und legt einen neuen an
