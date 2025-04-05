<script setup lang="ts">
import { ref } from "vue";
import { fetch } from "@tauri-apps/plugin-http";
import { trace } from "@tauri-apps/plugin-log";
import { startScan, wifiProvision, onDevice } from "tauri-plugin-provision-api";

const found_address = ref("");
const found_name = ref("");
const proof_of_possession = ref("abcd1234");
const ssid = ref("");
const password = ref("");

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


const scan = async () => {
  found_name.value = '';
  onDevice((device) => {
    console.log(`Device found: ${device}`);
    found_address.value = device.address;
    found_name.value = device.name;
  });
  startScan().then(updateResponse).catch(updateResponse);
}

const provision = async () => {
  console.log(`Provisioning ${found_name.value} with ${proof_of_possession.value} and ${ssid.value} and ${password.value}`);
  wifiProvision({
    address: found_address.value,
    pop: proof_of_possession.value,
    ssid: ssid.value,
    password: password.value,
  }).then(updateResponse).catch(updateResponse);
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
  trace(`Getting settings from ${apiUrl.value}/config`);
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
        <v-btn @click="scan">
          Start scan
        </v-btn><br>
      </v-form>
      <v-form v-if="found_name !== ''">
          Provision with:<br>
          <v-text-field v-model="proof_of_possession" label="Proof of possession" required outlined></v-text-field>
          <v-text-field v-model="ssid" label="Access point:" required outlined></v-text-field>
          <v-text-field v-model="password" label="Password" required outlined></v-text-field>
          <v-btn @click="provision">Provision {{ found_name }}</v-btn>
      </v-form>
      <v-divider></v-divider>
      <v-form ref="form" v-if="apiUrl !== ''" v-model="valid" lazy-validation>
        URL: {{ apiUrl }}<br>
        <v-btn color="primary" @click="get_settings">
          Refresh settings
        </v-btn>
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
      {{ response }}
    </v-container>
  </v-app>
</template>

<style scoped></style>
