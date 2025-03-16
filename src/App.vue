<script setup lang="ts">
import { ref } from "vue";
import { fetch } from "@tauri-apps/plugin-http";
import { trace } from "@tauri-apps/plugin-log";
import { ping } from "tauri-plugin-provision-api";

const treshold_ok = ref(0);
const treshold_nok = ref(0);
const charge_hours = ref(0);

const apiUrl = ref("");
const ipAddress = ref("");
const valid = ref(true);

const response = ref("");

function updateResponse(returnValue: any) {
		response.value += `[${new Date().toLocaleTimeString()}] ` + (typeof returnValue === 'string' ? returnValue : JSON.stringify(returnValue)) + '<br>'
	}


const _ping = async () => {
  ping("Pong!").then(updateResponse).catch(updateResponse);
}

const submit = async () => {
  apiUrl.value = `http://${ipAddress.value}`
  await get_settings();
}

const update = async () => {
  let body: String = JSON.stringify({
    treshold_ok: treshold_ok.value,
    treshold_nok: treshold_nok.value,
    charge_hours: charge_hours.value,
  });
  trace(`${body}`);
  const response = await fetch(`${apiUrl.value}/config`, {
    method: 'POST',
    headers: {
      'content-type': 'application/json;charset=UTF-8',
    },
    body: JSON.stringify({
      treshold_ok: Number(treshold_ok.value),
      treshold_nok: Number(treshold_nok.value),
      charge_hours: Number(charge_hours.value),
    })
  });
  trace(`Response statuss: ${response.status}`);
  await get_settings();
}

const get_settings = async () => {
  const response = await fetch(`${apiUrl.value}/config`, {
    method: 'GET',
  });
  trace(`Response statuss: ${response.status}`);
  const resp_json = await response.json();
  treshold_nok.value = resp_json.treshold_nok;
  treshold_ok.value = resp_json.treshold_ok;
  charge_hours.value = resp_json.charge_hours;
}

const reset = () => {
  apiUrl.value = ''
}

</script>

<template>
  <v-app>
    <v-container>
      <h3>Smart plug management</h3>
      <v-form ref="form" v-if="apiUrl === ''" v-model="valid" lazy-validation>
        <v-text-field v-model="ipAddress" label="IP Address" required outlined
          placeholder="Enter Smart Plug IP Address with dots"></v-text-field>

        <v-btn :disabled="!valid" color="primary" @click="submit">
          Submit
        </v-btn>
        <v-btn @click="_ping">
          ping
        </v-btn>
        Ping response: {{ response }}
      </v-form>
      <v-form ref="form" v-if="apiUrl !== ''" v-model="valid" lazy-validation>
        URL: {{ apiUrl }}<br>
        <v-btn color="secondary" @click="reset">
          Reset URL
        </v-btn>
        <v-divider></v-divider>
        <v-text-field v-model="treshold_ok" label="Treshold OK" required outlined></v-text-field>
        <v-text-field v-model="treshold_nok" label="Treshold NOK" required outlined></v-text-field>
        <v-text-field v-model="charge_hours" label="Charge hours" required outlined></v-text-field>

        <v-btn :disabled="!valid" color="primary" @click="update">
          Update settings
        </v-btn>
      </v-form>
    </v-container>
  </v-app>
</template>

<style scoped></style>
