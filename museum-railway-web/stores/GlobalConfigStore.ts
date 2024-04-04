import {defineStore} from 'pinia';

interface GlobalConfigState {
    initialHistoryCount: number;
}

export const useGlobalConfigStore = defineStore('globalConfigStore', {
    state: (): GlobalConfigState => ({
        initialHistoryCount: -1,
    }),
    getters: {
        historyIsEmpty: (state: GlobalConfigState): boolean => {
            return (window.history.length - state.initialHistoryCount) === 0;
        }
    },
    actions: {
        initHistoryCount() {
            this.initialHistoryCount = window.history.length
        }
    }
});