import '@mdi/font/css/materialdesignicons.min.css';
import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'


export const vuetify = createVuetify({
    components,
    directives,
    theme: {
        variations: {
            colors: ['primary'],
            lighten: 3,
            darken: 3
        }
    }
});
