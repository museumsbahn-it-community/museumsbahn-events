// Common event filter type that includes all filter criteria
export interface EventFilter {
  search?: string;
  states?: string[];
  fromDate?: Date;
  toDate?: Date;
  eventTypes?: string[];
  trainTypes?: string[];
  volunteer?: boolean;
  commercial?: boolean;
  tags?: string[];
}

// Type for available filter options
export interface EventFilterOptions {
  states: StateOption[];
  eventTypes: string[];
  trainTypes: string[];
  tags: string[];
}

// Helper type for state options
export interface StateOption {
  code: string;
  name: string;
}

// Type for filter counts
export interface FilterCounts {
  eventTypes: Record<string, number>;
  trainTypes: Record<string, number>;
  tags: Record<string, number>;
  states: Record<string, number>;
}

// Type for the filter update payload from EventFilters component
export interface EventFilterUpdate {
  dateRange: Date[];
  states: string[];
  eventTypes: string[];
  trainTypes: string[];
  isVolunteer: boolean;
  isCommercial: boolean;
  tags: string[];
}
