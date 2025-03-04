<script setup lang="ts">
import { ref } from "vue";
import { invoke } from "@tauri-apps/api/core";

const greetMsg = ref("");
const name = ref("");

async function greet() {
  // Learn more about Tauri commands at https://tauri.app/develop/calling-rust/
  greetMsg.value = await invoke("greet", { name: name.value });
}
</script>

<template>
  <NuxtLayout>
    <v-app>
      <h1>Welcome Smart outlet central</h1>
      <v-btn color="success">Test button</v-btn>
    </v-app>
  </NuxtLayout>
</template>

<style scoped>

</style>
