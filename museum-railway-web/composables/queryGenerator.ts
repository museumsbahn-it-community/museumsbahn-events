import {format} from "date-fns";

export function buildQuery(filters: EventFilterSettings): string {
    let queryParts = []
    if (filters.fromDate) {
        const formattedDate = format(filters.fromDate, "yyyy-MM-dd");
        queryParts.push(`"startDate" AFTER ${formattedDate}`);
    }
    if (filters.toDate) {
        const formattedDate = format(filters.toDate, "yyyy-MM-dd");
        queryParts.push(`"startDate" BEFORE ${formattedDate}`);
    }
    const tagFilterParts: string[] = []
    filters.tagFilters.forEach((tagFilter) => {
        const tagOptions = tagFilter.options.map((opt) => `"${tagFilter.key}" CONTAINS "${opt}"`)
        tagFilterParts.push(tagOptions.join(" OR "))
    })
    if (tagFilterParts.length > 0) {
        queryParts.push(...tagFilterParts)
    }

    return queryParts.join(" AND ");
}