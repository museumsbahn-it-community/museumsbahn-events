export function getImageUrlOrDefault(imageUrl: string | undefined): string {
    return imageUrl != null ? `/imgcache?url=${imageUrl}` : '/img/no_image.png';
}

export function getAltTextOrDefault(altText: string | undefined): string {
    return altText != null ? altText : 'Es ist leider keine Bildbeschreibung vorhanden.';
}

export function getCopyrightOrDefault(copyright: string | undefined): string | null {
    return copyright != null && !isBlank(copyright) ? copyright : null;
}

function isBlank(data: string): boolean {
    return data.replaceAll("\\s", "").length === 0;
}