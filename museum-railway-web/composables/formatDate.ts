import { format } from 'date-fns';
import { de } from 'date-fns/locale/de';

export function formatDate(date: Date | undefined): string {
    if (date == undefined) {
        return "";
    }
    return format(date, 'dd.MM.yyyy hh:mm', {locale: de});
}


