import { defineNuxtPlugin } from "nuxt/app";
import { inject, provide } from "vue";

const data: { [name: string]: string } = {
    'tags': 'Stichwörter',
    'history': 'Geschichte',
    'historic_train': 'Historischer Zug',
    'museum_train': 'Museumszug',
    'historic_train_trip': 'Historische Zugreise',
    'museum_railway': 'Musumsbahn',
    'special_trip': 'Sonderfahrt',
    'museum_event': 'Veranstaltung im Museum',
    'railway_museum': 'Eisenbahnmuseum',
    'narrow_gauge': 'Schmalspurbahn',
    'locomotive_type': 'Fahrzeugtyp',

    'diesel': 'Diesellok',
    'electric': 'Elektrisch',
    'steam': 'Dampfzug',
    'tram': 'Straßenbahn',

    'recurrence.type': 'Häufigkeit',
    'once': 'Einmalig',
    'regularly': 'Regelmäßig',
    'rarely': 'mehrmalige Wiederholung',

    'registration': 'Ticketart',
    'pre-sales-only': 'Nur im Vorverkauf',
    'ticket': 'Vor Ort erhältlich',
    'reservation-recommended': 'Reservierung empfohlen',
};

export default defineNuxtPlugin(() => {
    return {
        provide: {
            keyToString: (key: string) => data[key]
        }
    }
});
