Liest Kalender aus einem Google-Konto aus.

Wie finde ich die MannschaftsId?
---
Basketplan

Applikation BasketballCalendarSync:
---
Es wird ein Kalender mit Namen Basketballspiel gesucht

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
