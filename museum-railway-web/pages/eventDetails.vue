<template>
    <div class="m-2 sticky-event-details">
        <Button class="m-3" icon="pi pi-arrow-left" rounded outlined aria-label="Zurück"
            @click="navigateToEventList" />

        <EventDetails :museum-event="selectedEvent"
            :no-event-selected-placeholder-text="noEventSelectedPlaceholderText">
            Event Details
        </EventDetails>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import EventDetails from '~/components/EventDetails.vue';
import { EMPTY_EVENT_FILTERS, useEventListData } from '~/composables/eventListData';
import { useGlobalConfigStore } from '~/stores/GlobalConfigStore';


const router = useRouter();
const route = useRoute();

const eventKeyParam = route?.params?.eventKey;
const eventListData = useEventListData();
const selectedEvent = computed(() => eventListData.getEventByKey(eventKeyParam));
const globalConfig = useGlobalConfigStore();

onMounted(loadData);

async function loadData(): Promise<void> {
    await eventListData.loadEvents(EMPTY_EVENT_FILTERS);
}

function navigateToEventList(): void {
    if (globalConfig.historyIsEmpty) {
        router.push('/events'); // if history is empty go back to the events list
    } else {
        router.back();
    }
}

const noEventSelectedPlaceholderText = "Leider konnte die Veranstaltung nicht gefunden werden."
</script>