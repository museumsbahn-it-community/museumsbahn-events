<template>
  <div>
    <p>
      Die Analysedaten helfen uns dabei herauszufinden welche Funktionen besonders oft genutzt werden und welche gar nicht.
    </p>
    <p>
      Indem ihr Analysedaten mit uns teilt helft ihr uns die Seite besser zu machen. Alle Daten werden dabei anonymisiert, so
      dass keine Rückschlüsse auf einzelne User möglich sind.
    </p>
    <p>
      Danke für eure Unterstützung!
    </p>
  </div>
  <div v-if="!isUserOptedOut">
    <p>
      Analysedaten sind aktiviert.
    </p>
    <Button label="Analysedaten deaktivieren" @click="optOut()" outlined/>
  </div>
  <div v-else>
    <p>
      Analysedaten sind <b>deaktiviert</b>.
    </p>
    <Button class="light-text" label="Analysedaten aktivieren" @click="forgetOptOut()"/>
  </div>
</template>
<script setup lang="ts">

const isUserOptedOut = ref(false);

function updateOptOutStatus() {
  window._paq.push([function () {
    isUserOptedOut.value = this.isUserOptedOut();
  }]);
}

function optOut() {
  window._paq.push(['optUserOut']);
  updateOptOutStatus();
}

function forgetOptOut() {
  window._paq.push(['forgetUserOptOut']);
  updateOptOutStatus();
}

onMounted(() => updateOptOutStatus());

</script>