Kalender zur neuen Saison aktualisieren
---
Zur neuen Saison alle Mannschaften konfigurieren und die ligaId updaten.



Liest Kalender aus einem Google-Konto aus.

Wie finde ich die MannschaftsId?
---
Basketplan:
- Auf der Übersichtsseite unten links das Team anklicken. Der URL-Parameter teamId. Diese ID verändert sich nicht

Wie finde ich die Liga-ID?
---
basketplan
- Wenn man auf der Team-Seite ist, dann unten links auf Meisterschaft klicken und dann auf der Folgeseite die Saison auswählen.
- Der URL-Parameter leagueHoldiongId ist der gesuchte Parameter

Applikation BasketballCalendarSync:
---
Es wird ein Kalender mit Namen Basketballspiele gesucht

Web-OAuth erfolgt über ein Browserfenster, wenn der Service das erste mal gestartet wird.
Nach erfolgreicher OAUTH über Webbrowser-Fenster wird im Ordner tokens ein File erstellt.
Aktuell den Account carsten.lex@gmail.com verwenden und alle Rechte erlauben.

Falls es einen Access denied geben sollte beim Anlegen der Termine, 
dann einfach das File im tokens-Ordner löschen

Der Service kann mehrfach aufgerufen werden: alle vorhandenen Termine im Kalender werden 
zuerst gelöscht und dann alle Termine der aktuellen Saison wieder angelegt.
ABER: Es werden NUR diejenigen Termine gelöscht, die den Text "Found@Basketplan" in der
Description enthalten. Somit können auch manuell Einträge in den Kalender gemacht werden.


Applikation BasketballHeimspieleToFile
---
Alle Heimspiele unserer Mannschaften werden aus Probasket ausgelesen und nach Datum und Uhrzeit
sortiert in ein CSV-File geschrieben. Damit können wir dann den Offiziellen-Einsatzplan erstellen.
