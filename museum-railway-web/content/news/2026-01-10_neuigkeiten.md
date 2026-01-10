---
date: "2026-01-10"
title: "Redesign, Daten und mehr"
---
# Rückblick

2025 is um und da lohnt sich ein Rückblick auf das vergangene Jahr. 
Das Jahr 2025 war das erste Jahr in dem wir unsere Seite für Suchmaschinen optimiert haben und das hat man auch gemerkt.

Insgesamt 78.000 Impressionen und über 3000 Klicks haben wir über Google erzielt. Insgesamt haben wir 3600 Besucher
registriert (wenn man den Bot aus Singapur ignoriert, der die Webseite allein 2800 mal aufgerufen hat).

Man kann also sagen die Seite wird gut genutzt und das freut uns.

Ein Blick in die Suchbegriffe verrät außerdem, dass wir oft gefunden werden, wenn die Leute gar nicht nach einem Zug suchen.
Zum Beispiel beim Ostermarkt Kefermarkt, beim Gailtaler Speckfest oder dem Adventmarkt Admont. Das freut uns besonders, denn
das bedeutet, dass unser Ziel, die Museumsbahnen Österreichs einem breiteren Publikum zugänglich zu machen, durchaus erreicht wird.

# Neue Features

Mittlerweile ist schon über ein Jahr vergangen seit dem letzten Entwicklungs Update. Das ist leider so bei Projekten
die man eher nebenher macht. Aber hinter den Kulissen hat sich viel getan. Die wichtigsten Neuerungen im Überblick

## Mehr Informationen bei Museen

Die Datenkonfiguration für Museen wurde überarbeitet und erlaubt es jetzt mehr Daten zu hinterlegen. Insbesondere 
Link zu Google Maps, Beschreibungstext, Bilder und auch einen Link zu den Öffnungszeiten. 

Dadurch stehen für Besucher unserer Seite mehr Informationen zur Verfügung. Außerdem wurde die Datenverwaltung
überarbeitet, so dass die Datenverwaltung auch von Personen ohne Programmierkenntnisse gemacht werden kann.

## Filter in der Veranstaltungsliste

Ein sehr großes Feature welches nun endlich da ist, sind die Filter in der Veranstaltungsliste. Dadurch ist eine
verfeinerte Suche möglich. Das wiederum erlaubt uns noch mehr Museumsbahnen anzubinden ohne dass die Übersichtlichkeit verloren
geht.

Derzeit sind noch nicht alle Daten für alle Veranstaltungen komplett. Die Daten werden jetzt nach und nach verbessert. Wir bitten um
Geduld.

Das Design wurde so gestaltet, dass es sowohl auf dem PC als auch auf Smartphone gut zu bedienen ist. 

## Umstieg auf neues Webanalysetool

Im Hintergrund sind wir von Matomo auf Umami umgestiegen für die Webanalyse. Das hat primär den Grund, dass Matomo eigentlich
zu mächtig ist für das was wir brauchen und Umami schon von Haus aus auf Datenschutz setzt.

# Weiterer Ausblick

## Mehr und verbesserte Eventcollectoren

Leider passiert es immer wieder, dass unsere Eventcollectoren nicht mehr zuverlässig funktionieren, sei es durch 
Neugestaltung einer Webseite oder kleineren Umstrukturierungen. Deshalb werden wir demnächst wieder alle alten 
Eventcollectoren anschauen und gegebenenfalls reparieren.

Zusätzlich wird auch nochmal geschaut, dass die Collectors alle Daten für die Filter liefern.

## LLMs für Eventcollectoren

Ein weiteres Thema an dem wir dran sind ist der Einsatz von Large Language Models, einem Subset dessen was heutzutage als
KI bekannt ist, für unsere Eventcollectoren.

Vor allem das parsen von Datumsstrings wie "Jeden Montag von 1. Juli bis 30. September" oder ähnliche, bring manuell
programmierte Lösungen immer wieder an ihre Grenzen und macht Eventcollectoren unzuverlässig. LLMs können mit solchen
Sprachkonstrukten viel besser umgehen. Daher experimentieren wir mit ihrem Einsatz.

Ein weiterer Anwendungsfall wäre die Extraktion von Fahrtrouten und Fahrplänen aus unstrukturierten Daten.

Allerdngs muss beim Einsatz so einer Technologie sichergestellt werden, dass sie zuverlässig läuft und Fehler auf ein
Minimum reduziert werden, was die Entwicklung entsprechend langwierig macht.

Sollten hier Inhalte kommen, die mithilfe von KI aggregiert wurden, dann werden diese jedenfalls als solches gekennzeichnet!

## Manuelle Eventeingabe für Vereine

In letzter Zeit kam immer wieder die Anfrage von Vereinen ob man nicht manuell Events eingeben kann.
Eine Grundsatzentscheidung bei museumsbahn-events.at ist es keine volatilen Daten selbst zu speichern. Unsere Daten kommen
von den Veranstaltungslisten von Vereinen. Wir machen sie lediglich an einem Ort zugänglich. So ähnlich wie Google.

Das hat den einfachen Hintergrund, dass wir vermeiden wollen, dass veraltete Informationen bei uns angezeigt werden und
dass wir einfach nicht die zeitlichen Ressourcen aufbringen können um ständig Veranstaltungen upzudaten. 
(Im Endeffekt ist diese Seite bis jetzt das Hobbyprojekt eines einzelnen Softwareentwicklers, mit Unterstützung des Teams von boudicca.events.)

Nichtsdestotrotz haben wir erkannt, dass Bedarf besteht, zumal einige Vereine nicht mal eine eigene Webseite mit
Veranstaltungsinformationen haben. Deshalb arbeiten wir hier an einer Lösung die vermutlich mittels Google Forms oBesuchder 
Google Sheets die Eingabe von Terminen ermöglichen wird. Die Verantwortung für die Daten liegt dann weiterhin bei den Vereinen.

# Zusammenarbeit mit dem ÖMT

Wir durften unser Projekt bei der Herbsttagung 2025 des Verbands der österreichischen Museums und Touristikbahnen (ÖMT) präsentieren.
Die Rückmeldungen auf das Projekt waren sehr positiv. Im Zuge dessen ist auch die Frage aufgekommen ob wir mit dem ÖMT
zusammenarbeiten wollen. Es freut uns, dieses Vertrauen zu bekommen, aber momentan wollen wir lieber unabhängig bleiben und das hat 
vor allem 2 Gründe:

- Nicht alle Vereine und Museen die wir hier auflisten sind Mitglieder im ÖMT. Ziel dieser Seite ist es einen möglichst kompletten
Überblick zu geben. Wenn museumsbahn-events.at dann aber als Service des ÖMT angeboten würde, könnte dann müsste man sich einen Weg überlegen mit dieser Situation umzugehen.
- Der ÖMT hat selbst keine Ressourcen um die Entwicklung der Plattform voranzutreiben. Gleichzetig müsste man aber eine 
gewisse Verfügbarkeit und Features garantieren, wenn die Plattform als Service des ÖMT angeboten würde. Das ist als 
Hobbyprojekt einfach nicht möglich und von daher bleibt es genau das. Ein kleines Hobbyprojekt, das entwickelt wird wenn Zeit ist.

Dennoch sind wir mit denn Mitgliedern des ÖMT in Kontakt und schauen, dass wir gemeinsam unsere Museumsbahnen stärken.

Zur Erinnerung: Der Quellcode der Plattform ist auch als Open Source auf Github verfügbar und jeder kann bei Bedarf
Features beisteuern. Auch für die Datenverwaltung sind wir immer wieder froh, wenn wir Unterstützung bekommen.