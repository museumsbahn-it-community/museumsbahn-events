import { useState, computed } from '#app';

export function useGlobalConfig() {
  // Create reactive state
  const initialHistoryCount = useState<number>('initialHistoryCount', () => -1);

  // Computed property (equivalent to getter)
  const historyIsEmpty = computed(() => {
    return (window.history.length - initialHistoryCount.value) === 0;
  });

  // Method (equivalent to action)
  const initHistoryCount = () => {
    initialHistoryCount.value = window.history.length;
  };

  return {
    // State
    initialHistoryCount,
    
    // Computed property
    historyIsEmpty,
    
    // Method
    initHistoryCount,
  };
}