<template>
    <div style="display: flex; width: 80%">
        <v-text-field cache-items class="mx-3" flat hide-no-data hide-details label="Search for momos"
                      solo-inverted v-model="term" :loading="loading" @keydown.enter="search">
        </v-text-field>
        <v-btn icon :loading="loading" @click.prevent="search">
            <v-icon>search</v-icon>
        </v-btn>
    </div>
</template>

<script>
    let db
    export default {
        data() {
            return {
                term: '',
                loading: false
            }
        },
        created() {
            db = firebase.firestore()
        },
        mounted() {
            bus.$off('searchLoading')
            bus.$on('searchLoading', () => {
                this.loading = true
            })
            bus.$off('searchFinishedLoading')
            bus.$on('searchFinishedLoading', () => {
                this.loading = false
            })
        },
        methods: {
            search() {
                if (this.loading) {
                    return
                }
                this.loading = true
                if (this.$router.currentRoute.name !== 'momos') {
                    this.$router.push({name: 'momos'})
                }
                bus.$emit('search', this.term)
            }
        }
    }
</script>