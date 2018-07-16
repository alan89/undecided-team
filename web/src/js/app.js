require('./bootstrap');

window.Vue = require('vue');

// :: Vue Global Components

Vue.component('example-component', require('./components/ExampleComponent.vue'));

const app = new Vue({
    el: '#app'
});
