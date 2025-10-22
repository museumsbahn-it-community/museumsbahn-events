// Common event filter type that includes all filter criteria
export interface EventFilter {
  search?: string;
  states?: string[];
  fromDate?: Date;
  toDate?: Date;
  eventCategories?: string[];
  vehicleTypes?: string[];
  volunteer?: boolean;
  commercial?: boolean;
  tags?: string[];
}

// Type for available filter options
export interface EventFilterOptions {
  states: StateOption[];
  eventCategories: string[];
  vehicleTypes: string[];
  tags: string[];
}

// Helper type for state options
export interface StateOption {
  code: string;
  name: string;
}

// Type for filter counts
export interface FilterCounts {
  eventCategories: Record<string, number>;
  vehicleTypes: Record<string, number>;
  tags: Record<string, number>;
  states: Record<string, number>;
}

// Type for the filter update payload from EventFilters component
export interface EventFilterUpdate {
  dateRange: Date[];
  states: string[];
  eventCategories: string[];
  vehicleTypes: string[];
  isVolunteer: boolean;
  isCommercial: boolean;
  tags: string[];
}
