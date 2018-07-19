<template>
    <v-flex text-xs-center centered full-width full-height>
        <h2>Welcome to FireMomo</h2>
        <p>Please sign in to begin browsing the biggest meme collection on the Internet...</p>
        <div id="firebaseui-auth-container"></div>
    </v-flex>
</template>

<script>
    export default {
        mounted() {
            // Initialize the FirebaseUI Widget using Firebase.
            let ui = new firebaseui.auth.AuthUI(firebase.auth());
            let uiConfig = {
                signInOptions: [
                    firebase.auth.GoogleAuthProvider.PROVIDER_ID,
                    firebase.auth.FacebookAuthProvider.PROVIDER_ID,
                    firebase.auth.EmailAuthProvider.PROVIDER_ID
                ],
                tosUrl: 'https://google.com',
                callbacks: {
                    signInSuccessWithAuthResult: (authResult, redirectUrl) => {
                        ui.delete()
                        this.$router.push({name: 'momos'})
                    },
                    signInFailure: (error) => {
                        console.log(error);
                        alert("Whoops! Something went wrong while logging you in... Please try again")
                        return
                    },
                }
            }
            // The start method will wait until the DOM is loaded.
            ui.start('#firebaseui-auth-container', uiConfig);
        }
    }
</script>

<style scoped>

</style>