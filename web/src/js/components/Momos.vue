<template>
    <v-container grid-list-md>
        <v-toolbar
                dark
                color="teal"
        >
            <v-toolbar-title>Momo Search</v-toolbar-title>
            <v-text-field
                    v-on:keydown.enter="getMomos"
                    :loading="loading"
                    v-model="query"
                    cache-items
                    class="mx-3"
                    flat
                    hide-no-data
                    hide-details
                    label="Input your search criteria..."
                    solo-inverted
            ></v-text-field>
            <v-btn icon :loading="loading" @click.prevent="getMomos">
                <v-icon>search</v-icon>
            </v-btn>
        </v-toolbar>
        <!-- Sub Header -->
        <v-subheader>
            Trending Momos
        </v-subheader>

        <!-- Meme List -->
        <v-layout row wrap>
            <v-flex v-for="momo in momos" :key="momo.id" xs12 sm6 md4 align-start>
                <v-card>
                    <a href="#" @click.prevent="goToMomo(momo.id)">
                        <v-card-media contain class="white--text" :src="momo.imageUrl" height="200"
                                      color="blue"></v-card-media>
                    </a>
                    <v-card-title full-width>
                        <div style="width:100%">
                            <span class="grey--text">{{ momo.title }}</span>
                            <br/>
                            <span>
                                <small>by <b>{{ momo.userName }}</b> on <b>{{ getParsedDate(momo.createdAt) }}</b></small>
                            </span>
                            <v-layout row wrap>
                                <v-flex xs4 text-xs-center>
                                    <like-button :momo.sync="momo"></like-button>
                                </v-flex>
                                <v-flex xs4 text-xs-center>
                                    <v-btn flat color="info" class="white--text" @click.prevent="goToMomo(momo.id)">
                                        {{ momo.commentCount }}
                                        <v-icon right dark>comment</v-icon>
                                    </v-btn>
                                </v-flex>
                                <v-flex xs4 text-xs-center>
                                    <a :href="momo.imageUrl" download target="_blank" style="text-decoration: none">
                                        <v-btn flat icon color="purple">
                                            <v-icon>archive</v-icon>
                                        </v-btn>
                                    </a>
                                </v-flex>
                            </v-layout>
                        </div>
                    </v-card-title>
                </v-card>
            </v-flex>
        </v-layout>


        <!-- Floating Add Button -->
        <v-btn fixed dark fab bottom right color="pink" @click.prevent="goToAdd">
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
                momos: [],
                query: '',
                loading: false
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

                this.loading = true
                this.momos = []

                let momosQuery = db.collection("posts")

                // :: Add search values query
                if (this.query != "") {
                    momosQuery = momosQuery.where(`tags.${this.query}`, '==', true)
                }

                momosQuery = momosQuery.limit(50)

                // :: Perform query
                momosQuery.get().then((querySnapshot) => {
                    querySnapshot.forEach((doc) => {

                        this.loading = false
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

                    // :: Sort by creation date
                    this.momos.sort((a, b) => {
                        return new Date(b.createdAt) - new Date(a.createdAt);
                    })

                }).catch(err => {
                    console.log(err)
                    this.loading = false
                    bus.$emit('showSnackbar', 'Whoops! Something went wrong while retrieving the momos. Please try again...')
                });
            },

            goToMomo(id) {
                return this.$router.push({name: 'detail', params: {id}})
            },

            goToAdd() {
                return this.$router.push({name: 'create'})
            },

            getParsedDate(date) {
                return moment(date).format('MMMM Do YYYY')
            }

        }
    }
</script>