require('./bootstrap')

window.Vue = require('vue')

window.bus = new Vue()


// :: Vue Router

import VueRouter from 'vue-router'

Vue.use(VueRouter)

import router from './router/routes'

// :: Vuetify

import Vuetify from 'vuetify'

Vue.use(Vuetify)

// :: Vue Global Components

Vue.component('search-bar', require('./components/SearchBar'))

const app = new Vue({
    router,
    data: () => ({
        user: false,
        snackbar: false,
        snackbarText: '',
        backButtonVisible: false
    }),
    watch: {
        $route(to, from) {
            this.computeBackButtonsVisible()
        }
    },
    props: {
        source: String
    },
    el: '#app',
    created() {
        let config = {
            apiKey: "AIzaSyBLgsN1hYT92sr-hMFq_xsgJhJLU1ZtXq4",
            authDomain: "firemomo-bd063.firebaseapp.com",
            databaseURL: "https://firemomo-bd063.firebaseio.com",
            projectId: "firemomo-bd063",
            storageBucket: "firemomo-bd063.appspot.com",
            messagingSenderId: "483311387463"
        };
        firebase.initializeApp(config);
    },
    mounted() {

        this.computeBackButtonsVisible()

        /*
         * Listen to auth state events
         */
        firebase.auth().onAuthStateChanged((user) => {
            if (user) {
                // :: User is signed in.
                this.user = user
                if (this.$router.currentRoute.name === 'login') {
                    return this.$router.push({name: 'momos'})
                }
            } else {
                // :: User is signed out
                if (this.$router.currentRoute.name !== 'login' && this.$router.currentRoute.name !== 'register') {
                    return this.$router.push({name: 'login'})
                }
            }
        }, (error) => {
            console.log(error);
        });

        bus.$off('showSnackbar')
        bus.$on('showSnackbar', text => {
            this.snackbarText = text
            this.snackbar = true
        })
    },
    methods: {
        computeBackButtonsVisible() {
            this.backButtonVisible = (this.$router.currentRoute.name === 'detail' || this.$router.currentRoute.name === 'create')
        },
        logout() {
            firebase.auth().signOut().then(() => {
                this.user = false
                this.$router.push({name: 'login'})
            })
        },
        goBack() {
            return this.$router.push({name: 'momos'})
        }
    }
});
