<template>
    <v-container grid-list-md>
        <v-subheader>
            Momo List
        </v-subheader>
        <v-layout row wrap>
            <v-flex v-for="momo in momos" :key="momo.id" xs12 sm6 md4 align-start>
                <v-card>
                    <a href="#" @click.prevent="goToMomo(momo.id)">
                        <v-card-media contain class="white--text" :src="momo.imageUrl" height="180"></v-card-media>
                    </a>
                    <v-card-title>
                        <span class="grey--text">{{ momo.title }}</span>
                        <br/><br/>
                        <span>
                            <small>by <b>{{ momo.userName }}</b></small>
                        </span>
                        <v-badge right>
                            <span slot="badge">{{ Object.keys(momo.likes).length }}</span>
                            <v-icon small color="grey lighten-1">
                                thumb_up
                            </v-icon>
                        </v-badge>

                        <v-badge right>
                            <span slot="badge">{{ momo.commentCount }}</span>
                            <v-icon small color="grey lighten-1">
                                comment
                            </v-icon>
                        </v-badge>
                    </v-card-title>
                </v-card>
            </v-flex>
        </v-layout>
        <v-btn absolute dark fab top right color="pink" @click.prevent="goToAdd">
            <v-icon>add</v-icon>
        </v-btn>
    </v-container>
</template>

<script>
    let db
    export default {
        data() {
            return {
                momos: []
            }
        },
        created() {
            db = firebase.firestore()
            this.getMomos()
        },
        methods: {
            getMomos() {
                db.collection("posts").get().then((querySnapshot) => {
                    querySnapshot.forEach((doc) => {
                        let data = doc.data()
                        data.id = doc.id
                        this.momos.push(data)
                    });
                });
            },
            goToMomo(id) {
                return this.$router.push({name: 'detail', params: {id}})
            },
            goToAdd() {
                return this.$router.push({name: 'create'})
            }
        }
    }
</script>