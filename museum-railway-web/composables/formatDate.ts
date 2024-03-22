import { format } from 'date-fns';
import { de } from 'date-fns/locale/de';

export function formatDate(date: Date | undefined): string {
    if (date == undefined) {
        return "";
    }
    // TODO: add hh:mm once the eventcollectors support fetching also the time
    return format(date, 'dd.MM.yyyy', {locale: de});
}

export function formatDayMon(date: Date | undefined): string {
    if (date == undefined) {
        return "";
    }
    return format(date, 'dd MMM', {locale: de});
}

export function formatYear(date: Date | undefined): string {
    if (date == undefined) {
        return "";
    }
    return format(date, 'yyyy', {locale: de});
}