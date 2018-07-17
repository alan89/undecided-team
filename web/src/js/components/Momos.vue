<template>
    <v-container grid-list-md>
        <v-subheader>
            Trending Momos
        </v-subheader>
        <v-layout row wrap>
            <v-flex v-for="momo in momos" :key="momo.id" xs12 sm6 md4 align-start>
                <v-card height="335">
                    <a href="#" @click.prevent="goToMomo(momo.id)">
                        <v-card-media contain class="white--text" :src="momo.imageUrl" height="200"></v-card-media>
                    </a>
                    <v-card-title>
                        <div>
                            <span class="grey--text">{{ momo.title }}</span>
                            <br/>
                            <span>
                                <small>by <b>{{ momo.userName }}</b></small>
                            </span>
                            <v-container grid-list-md text-xs-center>
                                <v-layout row wrap>
                                    <v-flex xs6>
                                        <like-button :momo.sync="momo"></like-button>
                                    </v-flex>
                                    <v-flex xs6>
                                        <v-btn small color="info" class="white--text">
                                            {{ momo.commentCount }}
                                            <v-icon right dark>comment</v-icon>
                                        </v-btn>
                                    </v-flex>
                                </v-layout>
                            </v-container>
                        </div>
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
    import LikeButton from './LikeButton'

    let db
    export default {
        data() {
            return {
                momos: []
            }
        },
        components: {
            LikeButton
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

                        // :: Listen for realtime updates
                        db.collection('posts').doc(doc.id).onSnapshot(snapshot => {

                            let newData = snapshot.data()
                            newData.id = snapshot.id
                            let foundIndex = this.momos.findIndex(momo => {
                                return momo.id === newData.id
                            })
                            if (foundIndex > -1) {
                                this.$set(this.momos, foundIndex, newData)
                            }
                        })

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